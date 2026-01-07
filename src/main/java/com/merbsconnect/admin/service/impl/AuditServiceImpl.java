package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.response.AuditLogResponse;
import com.merbsconnect.admin.model.AuditLog;
import com.merbsconnect.admin.repository.AuditLogRepository;
import com.merbsconnect.admin.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Implementation of AuditService for audit logging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void logAction(String action, String entityType, Long entityId, String details) {
        try {
            // Get current user from security context
            String performedBy = getCurrentUsername();

            // Get IP address from request
            String ipAddress = getClientIpAddress();

            // Create audit log entry
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .performedBy(performedBy)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} on {} ID {}", action, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage());
            // Don't throw exception - audit logging should not break the main flow
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(this::mapToAuditLogResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByUser(String username, Pageable pageable) {
        return auditLogRepository.findByPerformedBy(username, pageable)
                .map(this::mapToAuditLogResponse);
    }

    /**
     * Get current username from security context.
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.warn("Could not get current username: {}", e.getMessage());
        }
        return "SYSTEM";
    }

    /**
     * Get client IP address from HTTP request.
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.warn("Could not get client IP address: {}", e.getMessage());
        }
        return "UNKNOWN";
    }

    /**
     * Map AuditLog entity to AuditLogResponse DTO.
     */
    private AuditLogResponse mapToAuditLogResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .performedBy(auditLog.getPerformedBy())
                .timestamp(auditLog.getTimestamp())
                .details(auditLog.getDetails())
                .ipAddress(auditLog.getIpAddress())
                .build();
    }
}
