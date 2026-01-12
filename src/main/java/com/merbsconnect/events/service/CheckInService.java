package com.merbsconnect.events.service;

import com.merbsconnect.events.dto.request.CheckInRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.response.CheckInResponse;
import com.merbsconnect.events.dto.response.CheckInStatsResponse;
import com.merbsconnect.events.dto.response.RegistrationDetailsResponse;

/**
 * Service interface for event check-in operations.
 */
public interface CheckInService {

    /**
     * Registers a participant for an event with QR code generation.
     *
     * @param eventId         The event ID
     * @param registrationDto The registration details
     * @return The registration details including QR code
     */
    RegistrationDetailsResponse registerForEventV2(Long eventId, EventRegistrationDto registrationDto);

    /**
     * Checks in a participant using their registration token (from QR code).
     * Staff-only operation.
     *
     * @param eventId The event ID
     * @param request The check-in request with token
     * @return The check-in result
     */
    CheckInResponse checkIn(Long eventId, CheckInRequest request);

    /**
     * Gets registration details by token.
     *
     * @param token The registration token
     * @return The registration details
     */
    RegistrationDetailsResponse getRegistrationByToken(String token);

    /**
     * Gets check-in statistics for an event.
     * Staff-only operation.
     *
     * @param eventId The event ID
     * @return The check-in statistics
     */
    CheckInStatsResponse getCheckInStats(Long eventId);
}
