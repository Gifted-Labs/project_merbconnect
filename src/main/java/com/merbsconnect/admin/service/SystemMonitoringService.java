package com.merbsconnect.admin.service;

import com.merbsconnect.admin.dto.response.SystemStatsResponse;

import java.util.List;

/**
 * Service interface for system monitoring operations.
 */
public interface SystemMonitoringService {

    /**
     * Get comprehensive system statistics.
     *
     * @return System statistics response
     */
    SystemStatsResponse getSystemStats();

    /**
     * Get recent application logs.
     *
     * @param limit Maximum number of log entries to return
     * @return List of recent log entries
     */
    List<String> getRecentLogs(int limit);
}
