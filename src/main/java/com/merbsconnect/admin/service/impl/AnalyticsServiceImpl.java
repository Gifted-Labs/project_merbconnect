package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.response.*;
import com.merbsconnect.admin.model.AuditLog;
import com.merbsconnect.admin.repository.AuditLogRepository;
import com.merbsconnect.admin.service.AnalyticsService;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.enums.UserStatus;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.startright.entity.TShirtRequest;
import com.merbsconnect.startright.repository.TShirtRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of AnalyticsService for dashboard analytics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AuditLogRepository auditLogRepository;
    private final TShirtRequestRepository tshirtRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalyticsData() {
        log.info("Fetching analytics data");

        // User statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);

        // Event statistics
        long totalEvents = eventRepository.count();
        LocalDate today = LocalDate.now();
        long activeEvents = eventRepository.countByDateAfter(today);
        long completedEvents = eventRepository.countByDateBefore(today);

        // Registration statistics
        long totalRegistrations = eventRepository.findAll().stream()
                .mapToLong(event -> event.getRegistrations().size() +
                        (event.getRegistrationsV2() != null ? event.getRegistrationsV2().size() : 0))
                .sum();

        // Merchandise statistics
        long totalMerchandiseOrders = tshirtRequestRepository.count();

        // Top 5 events by registration count
        List<TopEventDto> topEvents = getTopEvents(5);

        // T-shirt size breakdown
        Map<String, Long> tshirtSizeBreakdown = getTshirtSizeBreakdown();

        // Registration trend (last 6 months)
        List<RegistrationTrendDto> registrationTrend = getRegistrationTrend(6);

        // Recent activity (last 10 audit logs)
        List<AuditLogResponse> recentActivity = getRecentActivity(10);

        // Calculate legacy growth percentages (simplified - comparing to theoretical
        // baseline)
        Double userGrowthPercent = 5.0; // Placeholder - would compare to last period
        Double eventGrowthPercent = 12.0;
        Double registrationGrowthPercent = 8.0;

        // Calculate distributions
        Map<String, Long> academicLevelDistribution = new HashMap<>();
        Map<String, Long> programDistribution = new HashMap<>();
        Map<String, Long> referralSourceDistribution = new HashMap<>();

        // Helper to update distribution maps
        for (Event event : eventRepository.findAll()) {
            // Process V1 registrations
            if (event.getRegistrations() != null) {
                event.getRegistrations().forEach(reg -> {
                    updateDistribution(programDistribution, reg.getProgram());
                    updateDistribution(academicLevelDistribution,
                            reg.getAcademicLevel() != null ? reg.getAcademicLevel().getDisplayName() : null);
                    updateDistribution(referralSourceDistribution,
                            reg.getReferralSource() != null ? reg.getReferralSource().getDisplayName() : null);
                });
            }

            // Process V2 registrations
            if (event.getRegistrationsV2() != null) {
                event.getRegistrationsV2().forEach(reg -> {
                    updateDistribution(programDistribution, reg.getProgram());
                    updateDistribution(academicLevelDistribution,
                            reg.getAcademicLevel() != null ? reg.getAcademicLevel().getDisplayName() : null);
                    updateDistribution(referralSourceDistribution,
                            reg.getReferralSource() != null ? reg.getReferralSource().getDisplayName() : null);
                });
            }
        }

        return AnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalEvents(totalEvents)
                .activeEvents(activeEvents)
                .completedEvents(completedEvents)
                .totalRegistrations(totalRegistrations)
                .totalMerchandiseOrders(totalMerchandiseOrders)
                .userGrowthPercent(userGrowthPercent)
                .eventGrowthPercent(eventGrowthPercent)
                .registrationGrowthPercent(registrationGrowthPercent)
                .topEvents(topEvents)
                .tshirtSizeBreakdown(tshirtSizeBreakdown)
                .registrationTrend(registrationTrend)
                .recentActivity(recentActivity)
                .academicLevelDistribution(academicLevelDistribution)
                .programDistribution(programDistribution)
                .referralSourceDistribution(referralSourceDistribution)
                .build();
    }

    /**
     * Get top N events by registration count.
     */
    private List<TopEventDto> getTopEvents(int limit) {
        try {
            List<Long> topEventIds = eventRepository.findTopEventIdsByRegistrationCount(limit);
            List<TopEventDto> topEvents = new ArrayList<>();

            for (Long eventId : topEventIds) {
                eventRepository.findById(eventId).ifPresent(event -> {
                    long regCount = event.getRegistrations().size() +
                            (event.getRegistrationsV2() != null ? event.getRegistrationsV2().size() : 0);
                    topEvents.add(TopEventDto.builder()
                            .id(event.getId())
                            .title(event.getTitle())
                            .registrationCount(regCount)
                            .build());
                });
            }

            return topEvents;
        } catch (Exception e) {
            log.error("Error fetching top events: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get T-shirt size breakdown.
     */
    private Map<String, Long> getTshirtSizeBreakdown() {
        try {
            List<TShirtRequest> requests = tshirtRequestRepository.findAll();
            return requests.stream()
                    .collect(Collectors.groupingBy(
                            req -> req.getTShirtSize() != null ? req.getTShirtSize().name() : "UNKNOWN",
                            Collectors.counting()));
        } catch (Exception e) {
            log.error("Error fetching T-shirt size breakdown: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Get registration trend for last N months.
     */
    private List<RegistrationTrendDto> getRegistrationTrend(int months) {
        try {
            List<RegistrationTrendDto> trend = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

            for (int i = months - 1; i >= 0; i--) {
                YearMonth ym = YearMonth.now().minusMonths(i);
                LocalDate startDate = ym.atDay(1);
                LocalDate endDate = ym.atEndOfMonth();

                // Count registrations in this month
                List<Event> eventsInMonth = eventRepository.findByDateBetween(startDate, endDate);
                long registrations = eventsInMonth.stream()
                        .mapToLong(event -> event.getRegistrations().size() +
                                (event.getRegistrationsV2() != null ? event.getRegistrationsV2().size() : 0))
                        .sum();

                trend.add(RegistrationTrendDto.builder()
                        .period(ym.format(formatter))
                        .registrations(registrations)
                        .build());
            }

            return trend;
        } catch (Exception e) {
            log.error("Error fetching registration trend: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get recent audit log entries.
     */
    private List<AuditLogResponse> getRecentActivity(int limit) {
        try {
            return auditLogRepository.findAll(
                    PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"))).stream()
                    .map(this::mapToAuditLogResponse).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching recent activity: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Map AuditLog entity to AuditLogResponse DTO.
     */
    private AuditLogResponse mapToAuditLogResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .performedBy(log.getPerformedBy())
                .timestamp(log.getTimestamp())
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .build();
    }

    private void updateDistribution(Map<String, Long> distribution, String key) {
        String safeKey = (key != null && !key.trim().isEmpty()) ? key.trim() : "Unknown";
        distribution.put(safeKey, distribution.getOrDefault(safeKey, 0L) + 1);
    }
}
