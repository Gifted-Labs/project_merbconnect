package com.merbsconnect.events.model;

import com.merbsconnect.enums.AcademicLevel;
import com.merbsconnect.enums.ReferralSource;
import com.merbsconnect.enums.ShirtSize;
import com.merbsconnect.enums.University;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "event_registrations_v2", uniqueConstraints = @UniqueConstraint(columnNames = { "event_id", "email" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "event")
@EqualsAndHashCode(exclude = "event")
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

    // ===== New University Student Fields =====

    /**
     * The program/course the student is studying (e.g., "Computer Science",
     * "Medicine")
     */
    private String program;

    /**
     * The academic level/year of the student
     */
    @Enumerated(EnumType.STRING)
    private AcademicLevel academicLevel;

    /**
     * The name of the university/institution
     */
    @Enumerated(EnumType.STRING)
    private University university;

    /**
     * How the registrant heard about the event
     */
    @Enumerated(EnumType.STRING)
    private ReferralSource referralSource;

    /**
     * If referralSource is OTHER, this field stores the custom source
     */
    private String referralSourceOther;

    @PrePersist
    public void onCreate() {
        this.registeredAt = LocalDateTime.now();
    }
}
