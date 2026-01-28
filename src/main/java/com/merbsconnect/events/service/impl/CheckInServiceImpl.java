package com.merbsconnect.events.service.impl;

import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.events.dto.request.CheckInRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.response.CheckInResponse;
import com.merbsconnect.events.dto.response.CheckInStatsResponse;
import com.merbsconnect.events.dto.response.RegistrationDetailsResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.EventRegistration;
import com.merbsconnect.events.repository.EventRegistrationRepository;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.service.CheckInService;

import com.merbsconnect.exception.ResourceNotFoundException;
import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.sms.service.SmsService;
import com.merbsconnect.util.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of CheckInService for managing event registrations and
 * check-ins.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

        private final EventRegistrationRepository registrationRepository;
        private final EventRepository eventRepository;
        private final QrCodeService qrCodeService;
        private final EmailService emailService;
        private final SmsService smsService;

        // Support admin contact details for t-shirt order notifications
        private static final String SUPPORT_ADMIN_EMAIL = "juliusadjeteysowah@gmail.com";
        private static final String SUPPORT_ADMIN_PHONE = "0543358413";

        @Override
        @Transactional
        public RegistrationDetailsResponse registerForEventV2(Long eventId, EventRegistrationDto registrationDto) {
                log.info("Registering participant for event {}: {}", eventId, registrationDto.getEmail());

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Event not found with id: " + eventId));

                // Check for duplicate registration
                if (registrationRepository.existsByEventIdAndEmail(eventId, registrationDto.getEmail())) {
                        throw new IllegalStateException("Email is already registered for this event");
                }

                // Generate unique registration token
                String registrationToken = UUID.randomUUID().toString();

                // Generate QR code
                String qrCodeBase64 = qrCodeService.generateTokenQrCode(registrationToken);

                // Handle shirt preference
                boolean needsShirt = Boolean.TRUE.equals(registrationDto.getNeedsShirt());

                EventRegistration registration = EventRegistration.builder()
                                .event(event)
                                .email(registrationDto.getEmail())
                                .name(registrationDto.getName())
                                .phone(registrationDto.getPhone())
                                .note(registrationDto.getNote())
                                .registrationToken(registrationToken)
                                .qrCodeBase64(qrCodeBase64)
                                .needsShirt(needsShirt)
                                .shirtSize(needsShirt ? registrationDto.getShirtSize() : null)
                                .build();

                EventRegistration savedRegistration = registrationRepository.save(registration);
                log.info("Registration created with id: {} and token: {}", savedRegistration.getId(),
                                registrationToken);

                // Send confirmation email with PDF attachment
                try {
                        emailService.sendRegistrationConfirmationEmail(
                                        registrationDto.getEmail(),
                                        registrationDto.getName(),
                                        event.getTitle(),
                                        event.getDate(),
                                        event.getTime(),
                                        event.getLocation(),
                                        qrCodeBase64,
                                        registrationToken);
                        log.info("Registration confirmation email sent to: {}", registrationDto.getEmail());
                } catch (Exception e) {
                        log.error("Failed to send registration confirmation email to {}: {}",
                                        registrationDto.getEmail(), e.getMessage());
                }

                // Send SMS notification to participant
                sendRegistrationSms(registrationDto, event);

                // If t-shirt selected, notify support admin
                if (needsShirt && registrationDto.getShirtSize() != null) {
                        notifyAdminAboutTshirtOrder(savedRegistration, event);
                }

                return mapToDetailsResponse(savedRegistration, event);
        }

        /**
         * Sends SMS notification to the participant after registration.
         */
        private void sendRegistrationSms(EventRegistrationDto registrationDto, Event event) {
                if (registrationDto.getPhone() == null || registrationDto.getPhone().isBlank()) {
                        log.info("No phone number provided, skipping SMS notification");
                        return;
                }

                try {
                        String dateStr = event.getDate() != null
                                        ? event.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                                        : "TBA";

                        String message = String.format(
                                        "Hi %s! You're registered for %s on %s. Check your email for your ticket with QR code. See you there! - MerbsConnect",
                                        registrationDto.getName(),
                                        event.getTitle(),
                                        dateStr);

                        BulkSmsRequest smsRequest = BulkSmsRequest.builder()
                                        .recipients(List.of(formatPhoneNumber(registrationDto.getPhone())))
                                        .message(message)
                                        .build();

                        smsService.sendBulkSms(smsRequest);
                        log.info("Registration SMS sent to: {}", registrationDto.getPhone());
                } catch (Exception e) {
                        log.error("Failed to send registration SMS to {}: {}",
                                        registrationDto.getPhone(), e.getMessage());
                }
        }

        /**
         * Notifies support admin about a t-shirt order via email and SMS.
         */
        private void notifyAdminAboutTshirtOrder(EventRegistration registration, Event event) {
                log.info("Notifying admin about t-shirt order for registration: {}", registration.getId());

                String shirtSize = registration.getShirtSize() != null
                                ? registration.getShirtSize().getDisplayName()
                                : "Not specified";

                // Send SMS to admin
                try {
                        String smsMessage = String.format(
                                        "NEW T-SHIRT ORDER: %s (%s) - Size: %s - Event: %s - Phone: %s",
                                        registration.getName(),
                                        registration.getEmail(),
                                        shirtSize,
                                        event.getTitle(),
                                        registration.getPhone() != null ? registration.getPhone() : "N/A");

                        BulkSmsRequest smsRequest = BulkSmsRequest.builder()
                                        .recipients(List.of(formatPhoneNumber(SUPPORT_ADMIN_PHONE)))
                                        .message(smsMessage)
                                        .build();

                        smsService.sendBulkSms(smsRequest);
                        log.info("T-shirt order SMS notification sent to admin");
                } catch (Exception e) {
                        log.error("Failed to send t-shirt order SMS to admin: {}", e.getMessage());
                }

                // TODO: Send email to admin when email service supports arbitrary recipients
                // For now, logging the order details
                log.info("T-SHIRT ORDER DETAILS - Name: {}, Email: {}, Size: {}, Event: {}, Phone: {}",
                                registration.getName(),
                                registration.getEmail(),
                                shirtSize,
                                event.getTitle(),
                                registration.getPhone());
        }

        /**
         * Formats phone number for Ghana (removes leading 0, adds 233).
         */
        private String formatPhoneNumber(String phone) {
                if (phone == null)
                        return "";
                String cleaned = phone.replaceAll("[^0-9]", "");
                if (cleaned.startsWith("0")) {
                        cleaned = "233" + cleaned.substring(1);
                }
                if (!cleaned.startsWith("233")) {
                        cleaned = "233" + cleaned;
                }
                return cleaned;
        }

        @Override
        @Transactional
        public CheckInResponse checkIn(Long eventId, CheckInRequest request) {
                log.info("Checking in participant for event {} with token: {}", eventId,
                                request.getRegistrationToken());

                // Find registration by token
                EventRegistration registration = registrationRepository
                                .findByRegistrationToken(request.getRegistrationToken())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Registration not found with token: "
                                                                + request.getRegistrationToken()));

                // Verify registration is for the correct event
                if (!registration.getEvent().getId().equals(eventId)) {
                        return CheckInResponse.builder()
                                        .success(false)
                                        .message("This QR code is for a different event")
                                        .build();
                }

                // Check if already checked in
                if (registration.isCheckedIn()) {
                        return CheckInResponse.builder()
                                        .success(false)
                                        .alreadyCheckedIn(true)
                                        .participantName(registration.getName())
                                        .participantEmail(registration.getEmail())
                                        .eventTitle(registration.getEvent().getTitle())
                                        .checkInTime(registration.getCheckInTime())
                                        .message("Participant has already checked in at "
                                                        + registration.getCheckInTime())
                                        .build();
                }

                // Perform check-in
                registration.setCheckedIn(true);
                registration.setCheckInTime(LocalDateTime.now());
                registrationRepository.save(registration);

                log.info("Check-in successful for {} at event {}", registration.getEmail(), eventId);

                return CheckInResponse.builder()
                                .success(true)
                                .participantName(registration.getName())
                                .participantEmail(registration.getEmail())
                                .eventTitle(registration.getEvent().getTitle())
                                .checkInTime(registration.getCheckInTime())
                                .message("Check-in successful! Welcome, " + registration.getName())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public RegistrationDetailsResponse getRegistrationByToken(String token) {
                log.debug("Fetching registration by token: {}", token);

                EventRegistration registration = registrationRepository.findByRegistrationToken(token)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Registration not found with token: " + token));

                return mapToDetailsResponse(registration, registration.getEvent());
        }

        @Override
        @Transactional(readOnly = true)
        public CheckInStatsResponse getCheckInStats(Long eventId) {
                log.debug("Fetching check-in stats for event {}", eventId);

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Event not found with id: " + eventId));

                long totalRegistrations = registrationRepository.countByEventId(eventId);
                long checkedInCount = registrationRepository.countByEventIdAndCheckedInStatus(eventId, true);
                long pendingCount = totalRegistrations - checkedInCount;

                double checkInPercentage = totalRegistrations > 0
                                ? (double) checkedInCount / totalRegistrations * 100
                                : 0.0;

                return CheckInStatsResponse.builder()
                                .eventId(eventId)
                                .eventTitle(event.getTitle())
                                .totalRegistrations(totalRegistrations)
                                .checkedInCount(checkedInCount)
                                .pendingCount(pendingCount)
                                .checkInPercentage(Math.round(checkInPercentage * 10.0) / 10.0)
                                .build();
        }

        /**
         * Maps an EventRegistration entity to a RegistrationDetailsResponse DTO.
         */
        private RegistrationDetailsResponse mapToDetailsResponse(EventRegistration registration, Event event) {
                return RegistrationDetailsResponse.builder()
                                .id(registration.getId())
                                .eventId(event.getId())
                                .eventTitle(event.getTitle())
                                .name(registration.getName())
                                .email(registration.getEmail())
                                .phone(registration.getPhone())
                                .note(registration.getNote())
                                .registrationToken(registration.getRegistrationToken())
                                .qrCodeBase64(registration.getQrCodeBase64())
                                .checkedIn(registration.isCheckedIn())
                                .checkInTime(registration.getCheckInTime())
                                .registeredAt(registration.getRegisteredAt())
                                .needsShirt(registration.isNeedsShirt())
                                .shirtSize(registration.getShirtSize())
                                .build();
        }
}
