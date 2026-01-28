package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.response.SystemStatsResponse;
import com.merbsconnect.admin.repository.AuditLogRepository;
import com.merbsconnect.admin.service.SystemMonitoringService;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.enums.UserStatus;
import com.merbsconnect.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of SystemMonitoringService for system health monitoring.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemMonitoringServiceImpl implements SystemMonitoringService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public SystemStatsResponse getSystemStats() {
        log.info("Fetching system statistics");

        // Get database statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
        long totalEvents = eventRepository.count();
        long totalAuditLogs = auditLogRepository.countBy();

        // Calculate total registrations
        long totalRegistrations = eventRepository.findAll().stream()
                .mapToLong(event -> event.getRegistrations().size())
                .sum();

        // Get JVM metrics
        Map<String, Object> metrics = getJvmMetrics();

        // Get uptime
        String uptime = getSystemUptime();

        return SystemStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalEvents(totalEvents)
                .totalRegistrations(totalRegistrations)
                .totalAuditLogs(totalAuditLogs)
                .uptime(uptime)
                .metrics(metrics)
                .build();
    }

    @Override
    public List<String> getRecentLogs(int limit) {
        // This is a placeholder - in production, you'd integrate with your logging
        // framework
        // For now, return a simple message
        List<String> logs = new ArrayList<>();
        logs.add("System monitoring active");
        logs.add("Application running normally");
        log.info("Fetching {} recent log entries", limit);
        return logs;
    }

    /**
     * Get JVM metrics (memory, CPU, etc.).
     */
    private Map<String, Object> getJvmMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            long usedMemory = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024); // MB
            long maxMemory = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024); // MB

            metrics.put("usedMemoryMB", usedMemory);
            metrics.put("maxMemoryMB", maxMemory);
            metrics.put("memoryUsagePercent", (usedMemory * 100.0) / maxMemory);
            metrics.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        } catch (Exception e) {
            log.error("Error getting JVM metrics: {}", e.getMessage());
        }

        return metrics;
    }

    /**
     * Get system uptime as a formatted string.
     */
    private String getSystemUptime() {
        try {
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            long uptimeMillis = runtimeBean.getUptime();

            long seconds = uptimeMillis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            return String.format("%d days, %d hours, %d minutes",
                    days, hours % 24, minutes % 60);
        } catch (Exception e) {
            log.error("Error getting system uptime: {}", e.getMessage());
            return "Unknown";
        }
    }
}
