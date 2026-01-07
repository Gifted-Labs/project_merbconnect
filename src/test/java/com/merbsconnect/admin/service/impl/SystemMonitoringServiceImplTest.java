package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.response.SystemStatsResponse;
import com.merbsconnect.admin.repository.AuditLogRepository;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.enums.UserStatus;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SystemMonitoringServiceImpl Tests")
class SystemMonitoringServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private SystemMonitoringServiceImpl systemService;

    @Test
    @DisplayName("getSystemStats should return comprehensive stats")
    void testGetSystemStats() {
        // Arrange
        Event event = new Event();
        event.setRegistrations(new HashSet<>());

        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByStatus(UserStatus.ACTIVE)).thenReturn(5L);
        when(eventRepository.count()).thenReturn(2L);
        when(auditLogRepository.countBy()).thenReturn(100L);
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));

        // Act
        SystemStatsResponse stats = systemService.getSystemStats();

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalUsers()).isEqualTo(10L);
        assertThat(stats.getActiveUsers()).isEqualTo(5L);
        assertThat(stats.getTotalEvents()).isEqualTo(2L);
        assertThat(stats.getTotalAuditLogs()).isEqualTo(100L);
        assertThat(stats.getMetrics()).isNotEmpty(); // Should verify JVM metrics are populated
    }

    @Test
    @DisplayName("getRecentLogs should return logs")
    void testGetRecentLogs() {
        // Act
        List<String> logs = systemService.getRecentLogs(10);

        // Assert
        assertThat(logs).isNotNull();
        assertThat(logs).isNotEmpty();
    }
}
