package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.response.AuditLogResponse;
import com.merbsconnect.admin.model.AuditLog;
import com.merbsconnect.admin.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditServiceImpl Tests")
class AuditServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    @DisplayName("logAction should save audit log")
    void testLogAction() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        // Act
        auditService.logAction("CREATE", "User", 1L, "details");

        // Assert
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("getAuditLogs should return paginated logs")
    void testGetAuditLogs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        AuditLog auditLog = AuditLog.builder()
                .id(1L)
                .action("CREATE")
                .entityType("User")
                .entityId(1L)
                .performedBy("admin")
                .timestamp(LocalDateTime.now())
                .build();

        when(auditLogRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(auditLog)));

        // Act
        Page<AuditLogResponse> result = auditService.getAuditLogs(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(auditLogRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getAuditLogsByUser should return logs for specific user")
    void testGetAuditLogsByUser() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        AuditLog auditLog = AuditLog.builder()
                .id(1L)
                .performedBy("admin")
                .build();

        when(auditLogRepository.findByPerformedBy(eq("admin"), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(auditLog)));

        // Act
        Page<AuditLogResponse> result = auditService.getAuditLogsByUser("admin", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(auditLogRepository).findByPerformedBy("admin", pageable);
    }
}
