package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for overall event statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatsResponse {

    private Long totalEvents;
    private Long upcomingEvents;
    private Long pastEvents;
    private Long totalRegistrations;
    private Double averageRegistrationsPerEvent;
}
