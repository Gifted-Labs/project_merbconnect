package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for registration statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStatsResponse {

    private Long totalRegistrations;
    private List<EventRegistrationCount> registrationsByEvent;
    private List<TopEventDto> topEventsByRegistrations;

    /**
     * Nested DTO for event registration count.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventRegistrationCount {
        private String eventName;
        private Long count;
    }

    /**
     * Nested DTO for top events.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopEventDto {
        private String eventTitle;
        private Long registrationCount;
    }
}
