package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for consolidated dashboard data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private EventStatsResponse overallStats;
    private List<EventResponse> recentEvents;
    private List<RegistrationStatsResponse.TopEventDto> topEvents;
    private String systemStatus; // HEALTHY, WARNING, ERROR
}
