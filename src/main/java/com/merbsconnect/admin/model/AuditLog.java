package com.merbsconnect.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for tracking audit trail of admin actions.
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, etc.

    @Column(nullable = false)
    private String entityType; // User, Event, Config, etc.

    private Long entityId;

    @Column(nullable = false)
    private String performedBy; // Username/email of who performed the action

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String details; // JSON or detailed description

    private String ipAddress;

    @PrePersist
    private void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
