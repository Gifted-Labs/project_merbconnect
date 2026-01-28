package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Response DTO for individual event analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAnalyticsResponse {

    private Long eventId;
    private String eventTitle;
    private LocalDate eventDate;
    private Long totalRegistrations;
    private Integer speakerCount;
    private String eventStatus; // UPCOMING, PAST, ONGOING
}
