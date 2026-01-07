package com.merbsconnect.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for audit log entries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private String performedBy;
    private LocalDateTime timestamp;
    private String details;
    private String ipAddress;
}
