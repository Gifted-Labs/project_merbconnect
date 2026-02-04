package com.merbsconnect.events.model;

import com.merbsconnect.enums.AcademicLevel;
import com.merbsconnect.enums.CheckInMethod;
import com.merbsconnect.enums.ReferralSource;
import com.merbsconnect.enums.ShirtSize;
import com.merbsconnect.enums.University;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Method used for check-in (MANUAL, QR_SCAN, BULK).
     * Nullable for backward compatibility with existing records.
     */
    @Enumerated(EnumType.STRING)
    private CheckInMethod checkInMethod;

    private LocalDateTime registeredAt;

    /**
     * Whether the participant needs a t-shirt.
     */
    @Builder.Default
    private boolean needsShirt = false;

    /**
     * Legacy: Simple shirt size field.
     * Kept for backward compatibility with existing records.
     * New registrations should use merchandiseOrders instead.
     */
    @Enumerated(EnumType.STRING)
    private ShirtSize shirtSize;

    /**
     * Detailed merchandise orders (color, size, quantity).
     * Replaces simple shirtSize for new registrations.
     */
    @ElementCollection
    @CollectionTable(name = "registration_merchandise_orders", joinColumns = @JoinColumn(name = "registration_id"))
    @Builder.Default
    private List<MerchandiseOrder> merchandiseOrders = new ArrayList<>();

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

    /**
     * Returns a formatted string of all merchandise orders.
     */
    public String getMerchandiseOrdersDisplay() {
        if (merchandiseOrders == null || merchandiseOrders.isEmpty()) {
            // Fallback to legacy shirtSize if no orders
            if (shirtSize != null) {
                return shirtSize.getDisplayName();
            }
            return "No orders";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < merchandiseOrders.size(); i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(merchandiseOrders.get(i).getDisplayString());
        }
        return sb.toString();
    }
}
