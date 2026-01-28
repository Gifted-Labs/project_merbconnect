package com.merbsconnect.events.model;

import com.merbsconnect.enums.ShirtSize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an event registration with QR code and check-in support.
 * This is a proper entity (not embedded) to support check-in tracking and QR
 * codes.
 */
@Entity
@Table(name = "event_registrations_v2", uniqueConstraints = @UniqueConstraint(columnNames = { "event_id", "email" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String note;

    /**
     * Unique registration token for QR code.
     * This is encoded in the QR code for check-in scanning.
     */
    @Column(unique = true, nullable = false)
    private String registrationToken;

    /**
     * Base64 encoded QR code image (PNG format).
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String qrCodeBase64;

    @Builder.Default
    private boolean checkedIn = false;

    private LocalDateTime checkInTime;

    private LocalDateTime registeredAt;

    /**
     * Whether the participant needs a t-shirt.
     */
    @Builder.Default
    private boolean needsShirt = false;

    /**
     * Shirt size if needsShirt is true.
     */
    @Enumerated(EnumType.STRING)
    private ShirtSize shirtSize;

    @PrePersist
    public void onCreate() {
        this.registeredAt = LocalDateTime.now();
    }
}
