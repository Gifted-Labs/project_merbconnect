package com.merbsconnect.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for system statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatsResponse {

    private Long totalUsers;
    private Long activeUsers;
    private Long totalEvents;
    private Long totalRegistrations;
    private Long totalAuditLogs;
    private String uptime;
    private Map<String, Object> metrics; // Memory, CPU, etc.
}
