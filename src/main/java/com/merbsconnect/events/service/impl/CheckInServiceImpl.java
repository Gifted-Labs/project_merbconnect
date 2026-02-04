package com.merbsconnect.events.service.impl;

import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.events.dto.request.CheckInRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.request.MerchandiseOrderDto;
import com.merbsconnect.events.dto.response.CheckInResponse;
import com.merbsconnect.events.dto.response.CheckInStatsResponse;
import com.merbsconnect.events.dto.response.RegistrationDetailsResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.EventRegistration;
import com.merbsconnect.events.model.MerchandiseOrder;
import com.merbsconnect.events.repository.EventRegistrationRepository;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.service.CheckInService;

import com.merbsconnect.exception.ResourceNotFoundException;
import com.merbsconnect.enums.CheckInMethod;
import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.sms.service.SmsService;
import com.merbsconnect.util.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        // Admin contact details injected from application config
        @Value("${app.admin.email:merbsconnect@gmail.com}")
        private String adminEmail;

        @Value("${app.admin.phone:0543358413}")
        private String adminPhone;

        @Override
        @Transactional
        @org.springframework.cache.annotation.CacheEvict(value = "registrations", key = "#eventId")
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

                // Build merchandise orders from DTO
                List<MerchandiseOrder> merchandiseOrders = new ArrayList<>();
                if (needsShirt && registrationDto.getMerchandiseOrders() != null) {
                        merchandiseOrders = registrationDto.getMerchandiseOrders().stream()
                                        .map(dto -> MerchandiseOrder.builder()
                                                        .color(dto.getColor())
                                                        .size(dto.getSize())
                                                        .quantity(dto.getQuantity() != null ? dto.getQuantity() : 1)
                                                        .build())
                                        .collect(Collectors.toList());
                }

                EventRegistration registration = EventRegistration.builder()
                                .event(event)
                                .email(registrationDto.getEmail())
                                .name(registrationDto.getName())
                                .phone(registrationDto.getPhone())
                                .note(registrationDto.getNote())
                                .registrationToken(registrationToken)
                                .qrCodeBase64(qrCodeBase64)
                                .needsShirt(needsShirt)
                                // Legacy shirtSize for backward compatibility
                                .shirtSize(needsShirt ? registrationDto.getShirtSize() : null)
                                // New detailed merchandise orders
                                .merchandiseOrders(merchandiseOrders)
                                // New university student fields
                                .program(registrationDto.getProgram())
                                .academicLevel(registrationDto.getAcademicLevel())
                                .university(registrationDto.getUniversity())
                                .referralSource(registrationDto.getReferralSource())
                                .referralSourceOther(registrationDto.getReferralSourceOther())
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
                                        registrationDto.getEmail(), e.getMessage(), e);
                }

                // Send SMS notification to participant
                sendRegistrationSms(registrationDto, event);

                // If t-shirt selected, notify support admin
                if (needsShirt && (registrationDto.getShirtSize() != null || !merchandiseOrders.isEmpty())) {
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
                        log.error("Failed to send registration SMS to {}: ",
                                        registrationDto.getPhone(), e);
                }
        }

        /**
         * Notifies support admin about a t-shirt order via email and SMS,
         * and sends confirmation SMS to the user.
         */
        private void notifyAdminAboutTshirtOrder(EventRegistration registration, Event event) {
                log.info("Notifying admin about t-shirt order for registration: {}", registration.getId());

                // Get merchandise display string (handles both legacy and new format)
                String merchandiseDisplay = registration.getMerchandiseOrdersDisplay();

                // 1. Send SMS to admin
                try {
                        String smsMessage = String.format(
                                        "NEW T-SHIRT ORDER: %s (%s) - Items: %s - Event: %s - Phone: %s",
                                        registration.getName(),
                                        registration.getEmail(),
                                        merchandiseDisplay,
                                        event.getTitle(),
                                        registration.getPhone() != null ? registration.getPhone() : "N/A");

                        BulkSmsRequest smsRequest = BulkSmsRequest.builder()
                                        .recipients(List.of(formatPhoneNumber(adminPhone)))
                                        .message(smsMessage)
                                        .build();

                        smsService.sendBulkSms(smsRequest);
                        log.info("T-shirt order SMS notification sent to admin at: {}", adminPhone);
                } catch (Exception e) {
                        log.error("Failed to send t-shirt order SMS to admin: ", e);
                }

                // 2. Send Email to admin with detailed merchandise info
                try {
                        emailService.sendTshirtOrderAdminEmail(
                                        registration.getName(),
                                        registration.getEmail(),
                                        registration.getPhone(),
                                        merchandiseDisplay,
                                        event.getTitle());
                        log.info("T-shirt order email notification sent to admin at: {}", adminEmail);
                } catch (Exception e) {
                        log.error("Failed to send t-shirt order email to admin: ", e);
                }

                // 3. Send confirmation SMS to user
                sendTshirtConfirmationSmsToUser(registration, event);

                log.info("T-SHIRT ORDER DETAILS - Name: {}, Email: {}, Items: {}, Event: {}, Phone: {}",
                                registration.getName(),
                                registration.getEmail(),
                                merchandiseDisplay,
                                event.getTitle(),
                                registration.getPhone());
        }

        /**
         * Sends a confirmation SMS to the user after their T-shirt order.
         */
        private void sendTshirtConfirmationSmsToUser(EventRegistration registration, Event event) {
                if (registration.getPhone() == null || registration.getPhone().isBlank()) {
                        log.info("No phone number provided for T-shirt confirmation SMS, skipping");
                        return;
                }

                try {
                        // Extract first name from full name
                        String firstName = registration.getName();
                        if (firstName != null && firstName.contains(" ")) {
                                firstName = firstName.split(" ")[0];
                        }

                        String message = String.format(
                                        "Hello %s, your T-shirt order for %s has been received successfully. Our team will contact you shortly for the next steps. Thank you! - MerbsConnect",
                                        firstName,
                                        event.getTitle());

                        BulkSmsRequest smsRequest = BulkSmsRequest.builder()
                                        .recipients(List.of(formatPhoneNumber(registration.getPhone())))
                                        .message(message)
                                        .build();

                        smsService.sendBulkSms(smsRequest);
                        log.info("T-shirt confirmation SMS sent to user: {}", registration.getPhone());
                } catch (Exception e) {
                        log.error("Failed to send T-shirt confirmation SMS to user {}: ",
                                        registration.getPhone(), e);
                }
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
                registration.setCheckInMethod(
                                request.getMethod() != null ? request.getMethod() : CheckInMethod.MANUAL);
                registrationRepository.save(registration);

                log.info("Check-in successful for {} at event {} via {}",
                                registration.getEmail(), eventId,
                                registration.getCheckInMethod());

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

                // Count by check-in method
                long qrScanCount = registrationRepository.countByEventIdAndCheckInMethod(eventId,
                                CheckInMethod.QR_SCAN);
                long manualCount = registrationRepository.countByEventIdAndCheckInMethod(eventId, CheckInMethod.MANUAL);

                return CheckInStatsResponse.builder()
                                .eventId(eventId)
                                .eventTitle(event.getTitle())
                                .totalRegistrations(totalRegistrations)
                                .checkedInCount(checkedInCount)
                                .pendingCount(pendingCount)
                                .checkInPercentage(Math.round(checkInPercentage * 10.0) / 10.0)
                                .qrScanCount(qrScanCount)
                                .manualCount(manualCount)
                                .build();
        }

        /**
         * Maps an EventRegistration entity to a RegistrationDetailsResponse DTO.
         */
        private RegistrationDetailsResponse mapToDetailsResponse(EventRegistration registration, Event event) {
                // Convert entity MerchandiseOrder to DTO
                List<MerchandiseOrderDto> merchandiseOrderDtos = null;
                if (registration.getMerchandiseOrders() != null && !registration.getMerchandiseOrders().isEmpty()) {
                        merchandiseOrderDtos = registration.getMerchandiseOrders().stream()
                                        .map(order -> MerchandiseOrderDto.builder()
                                                        .color(order.getColor())
                                                        .size(order.getSize())
                                                        .quantity(order.getQuantity())
                                                        .build())
                                        .collect(Collectors.toList());
                }

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
                                .merchandiseOrders(merchandiseOrderDtos)
                                .merchandiseOrdersDisplay(registration.getMerchandiseOrdersDisplay())
                                // New university student fields
                                .program(registration.getProgram())
                                .academicLevel(registration.getAcademicLevel())
                                .university(registration.getUniversity())
                                .referralSource(registration.getReferralSource())
                                .referralSourceOther(registration.getReferralSourceOther())
                                .build();
        }
}
