package com.merbsconnect.events.dto.response;

import com.merbsconnect.enums.AcademicLevel;
import com.merbsconnect.enums.ReferralSource;
import com.merbsconnect.enums.ShirtSize;
import com.merbsconnect.events.dto.request.MerchandiseOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for detailed registration information including QR code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationDetailsResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private String name;
    private String email;
    private String phone;
    private String note;
    private String registrationToken;
    private String qrCodeBase64;
    private boolean checkedIn;
    private LocalDateTime checkInTime;
    private LocalDateTime registeredAt;
    private boolean needsShirt;

    /**
     * Legacy: Simple shirt size.
     * Kept for backward compatibility.
     */
    private ShirtSize shirtSize;

    /**
     * Detailed merchandise orders (color, size, quantity).
     */
    private List<MerchandiseOrderDto> merchandiseOrders;

    /**
     * Display-friendly string of all merchandise orders.
     */
    private String merchandiseOrdersDisplay;

    // ===== University Student Information =====

    private String program;
    private AcademicLevel academicLevel;
    private com.merbsconnect.enums.University university;
    private ReferralSource referralSource;
    private String referralSourceOther;
}
