package com.merbsconnect.events.dto.response;

import com.merbsconnect.enums.AcademicLevel;
import com.merbsconnect.enums.ReferralSource;
import com.merbsconnect.enums.ShirtSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private ShirtSize shirtSize;

    // ===== University Student Information =====

    private String program;
    private AcademicLevel academicLevel;
    private String university;
    private String department;
    private ReferralSource referralSource;
    private String referralSourceOther;
    private String studentId;
    private String dietaryRestrictions;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
