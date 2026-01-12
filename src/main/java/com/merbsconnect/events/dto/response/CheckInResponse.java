package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for check-in operation result.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInResponse {

    private boolean success;
    private String participantName;
    private String participantEmail;
    private String eventTitle;
    private LocalDateTime checkInTime;
    private String message;
    private boolean alreadyCheckedIn;
}
