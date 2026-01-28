package com.merbsconnect.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for tracking user activity.
 */
@Entity
@Table(name = "activity_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String activityType; // PAGE_VIEW, API_CALL, LOGIN, LOGOUT, etc.

    private String endpoint; // API endpoint or page accessed

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String details; // Additional details about the activity

    @PrePersist
    private void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
