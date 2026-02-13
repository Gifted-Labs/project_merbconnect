package com.merbsconnect.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Comprehensive analytics response DTO containing all dashboard metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    // Summary counts
    private Long totalUsers;
    private Long activeUsers;
    private Long totalEvents;
    private Long activeEvents;
    private Long completedEvents;
    private Long totalRegistrations;
    private Long totalMerchandiseOrders;

    // Growth indicators (percentage change)
    private Double userGrowthPercent;
    private Double eventGrowthPercent;
    private Double registrationGrowthPercent;

    // Top events by registration count
    private List<TopEventDto> topEvents;

    // T-shirt size breakdown
    private Map<String, Long> tshirtSizeBreakdown;

    // Registration trend (monthly)
    private List<RegistrationTrendDto> registrationTrend;

    // Recent activity
    private List<AuditLogResponse> recentActivity;

    // Distributions
    private Map<String, Long> academicLevelDistribution;
    private Map<String, Long> programDistribution;
    private Map<String, Long> referralSourceDistribution;
}
