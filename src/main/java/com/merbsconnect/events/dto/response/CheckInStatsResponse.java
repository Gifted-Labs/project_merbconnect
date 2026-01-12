package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for check-in statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInStatsResponse {

    private Long eventId;
    private String eventTitle;
    private long totalRegistrations;
    private long checkedInCount;
    private long pendingCount;
    private double checkInPercentage;
}
