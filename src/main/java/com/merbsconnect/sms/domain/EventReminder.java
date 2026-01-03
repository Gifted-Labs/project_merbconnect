package com.merbsconnect.sms.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_reminders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventId;

    private Long templateId;

    @Column(nullable = false)
    private LocalDateTime reminderDate;

    private Integer daysBeforeEvent;

    @Enumerated(EnumType.STRING)
    private ReminderStatus status = ReminderStatus.SCHEDULED;

    private String failureReason;

    private Integer recipientCount = 0;

    private LocalDateTime executedAt;

    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }
}