package com.merbsconnect.admin.controller;

import com.merbsconnect.admin.dto.response.ActiveSessionResponse;
import com.merbsconnect.admin.dto.response.AuditLogResponse;
import com.merbsconnect.admin.service.AuditService;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for audit logging and security operations.
 * All endpoints require SUPER_ADMIN or ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:5173" }, allowedHeaders = { "*" }, maxAge = 3600)
public class AuditController {

    private final AuditService auditService;

    /**
     * Get all audit logs with pagination.
     * Requires SUPER_ADMIN or ADMIN role.
     *
     * @param pageable Pagination parameters
     * @return Page of audit logs
     *
     *
     * \
     *
     * @GetMapping("/audit-logs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(Pageable pageable) {
        try {
            log.info("Fetching audit logs with pagination");
            Page<AuditLogResponse> auditLogs = auditService.getAuditLogs(pageable);
            return new ResponseEntity<>(auditLogs, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching audit logs: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get active user sessions.
     * Requires SUPER_ADMIN role.
     * Note: This is a placeholder - actual session management depends on your
     * authentication setup.
     *
     * @return List of active sessions
     */
    @GetMapping("/security/sessions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ActiveSessionResponse>> getActiveSessions() {
        try {
            log.info("Fetching active sessions");
            // Placeholder - in production, integrate with Spring Security session registry
            List<ActiveSessionResponse> sessions = new ArrayList<>();
            return new ResponseEntity<>(sessions, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching active sessions: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Terminate a user session.
     * Requires SUPER_ADMIN role.
     * Note: This is a placeholder - actual session termination depends on your
     * authentication setup.
     *
     * @param sessionId Session ID to terminate
     * @return Success message
     */
    @DeleteMapping("/security/sessions/{sessionId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<MessageResponse> terminateSession(@PathVariable String sessionId) {
        try {
            log.info("Terminating session: {}", sessionId);
            // Placeholder - in production, integrate with Spring Security session registry
            return new ResponseEntity<>(
                    MessageResponse.builder()
                            .message(
                                    "Session termination feature requires Spring Security session management configuration")
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error terminating session: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
