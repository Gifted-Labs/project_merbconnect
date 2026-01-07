package com.merbsconnect.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for user activity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityResponse {

    private Long userId;
    private String username;
    private List<ActivityLogDto> activities;

    /**
     * Nested DTO for activity log.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityLogDto {
        private String activityType;
        private String endpoint;
        private LocalDateTime timestamp;
        private String details;
    }
}
