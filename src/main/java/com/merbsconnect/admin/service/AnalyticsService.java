package com.merbsconnect.admin.service;

import com.merbsconnect.admin.dto.response.AnalyticsResponse;

/**
 * Service interface for analytics operations.
 */
public interface AnalyticsService {

    /**
     * Get comprehensive analytics data for the admin dashboard.
     *
     * @return AnalyticsResponse containing all dashboard metrics
     */
    AnalyticsResponse getAnalyticsData();
}
