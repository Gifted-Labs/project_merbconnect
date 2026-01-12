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
import com.merbsconnect.util.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

                EventRegistration registration = EventRegistration.builder()
                                .event(event)
                                .email(registrationDto.getEmail())
                                .name(registrationDto.getName())
                                .phone(registrationDto.getPhone())
                                .note(registrationDto.getNote())
                                .registrationToken(registrationToken)
                                .qrCodeBase64(qrCodeBase64)
                                .build();

                EventRegistration savedRegistration = registrationRepository.save(registration);
                log.info("Registration created with id: {} and token: {}", savedRegistration.getId(),
                                registrationToken);

                // Send confirmation email with QR code
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
                        // Don't fail registration if email sending fails
                }

                return mapToDetailsResponse(savedRegistration, event);
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
                long checkedInCount = registrationRepository.countByEventIdAndCheckedIn(eventId, true);
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
                                .build();
        }
}
