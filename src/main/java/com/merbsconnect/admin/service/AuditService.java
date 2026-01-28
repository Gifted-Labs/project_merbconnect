package com.merbsconnect.admin.service;

import com.merbsconnect.admin.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for audit logging operations.
 */
public interface AuditService {

    /**
     * Log an admin action to the audit trail.
     *
     * @param action     Action performed (CREATE, UPDATE, DELETE, etc.)
     * @param entityType Type of entity affected
     * @param entityId   ID of the entity
     * @param details    Additional details about the action
     */
    void logAction(String action, String entityType, Long entityId, String details);

    /**
     * Get all audit logs with pagination.
     *
     * @param pageable Pagination information
     * @return Page of audit log responses
     */
    Page<AuditLogResponse> getAuditLogs(Pageable pageable);

    /**
     * Get audit logs for a specific user.
     *
     * @param username Username to filter by
     * @param pageable Pagination information
     * @return Page of audit log responses
     */
    Page<AuditLogResponse> getAuditLogsByUser(String username, Pageable pageable);
}
