package com.merbsconnect.events.controller;

import com.merbsconnect.events.dto.request.CheckInRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.response.CheckInResponse;
import com.merbsconnect.events.dto.response.CheckInStatsResponse;
import com.merbsconnect.events.dto.response.RegistrationDetailsResponse;
import com.merbsconnect.events.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for event check-in operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events/{eventId}")
@RequiredArgsConstructor
@Tag(name = "Event Check-in", description = "Event registration and check-in operations")
public class CheckInController {

    private final CheckInService checkInService;

    /**
     * Register for an event with QR code generation.
     * Public endpoint - anyone can register.
     */
    @PostMapping("/register-v2")
    @Operation(summary = "Register for event", description = "Register for an event and receive a QR code")
    public ResponseEntity<RegistrationDetailsResponse> registerForEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventRegistrationDto registrationDto) {

        log.info("Registering for event {}: {}", eventId, registrationDto.getEmail());
        RegistrationDetailsResponse response = checkInService.registerForEventV2(eventId, registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Check in a participant using QR code token.
     * Staff-only operation.
     */
    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    @Operation(summary = "Check in participant", description = "Check in a participant using their QR code token (staff-only)")
    public ResponseEntity<CheckInResponse> checkIn(
            @PathVariable Long eventId,
            @Valid @RequestBody CheckInRequest request) {

        log.info("Check-in request for event {}", eventId);
        CheckInResponse response = checkInService.checkIn(eventId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get check-in statistics for an event.
     * Staff-only operation.
     */
    @GetMapping("/check-in/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    @Operation(summary = "Get check-in stats", description = "Get check-in statistics for an event (staff-only)")
    public ResponseEntity<CheckInStatsResponse> getCheckInStats(@PathVariable Long eventId) {
        log.debug("Fetching check-in stats for event {}", eventId);
        CheckInStatsResponse stats = checkInService.getCheckInStats(eventId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get registration details by token.
     * Used for verifying QR code content.
     */
    @GetMapping("/registration")
    @Operation(summary = "Get registration details", description = "Get registration details by token")
    public ResponseEntity<RegistrationDetailsResponse> getRegistrationByToken(
            @RequestParam("token") String token) {

        log.debug("Fetching registration by token");
        RegistrationDetailsResponse response = checkInService.getRegistrationByToken(token);
        return ResponseEntity.ok(response);
    }
}
