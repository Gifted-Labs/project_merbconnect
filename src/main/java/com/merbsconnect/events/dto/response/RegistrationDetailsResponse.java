package com.merbsconnect.events.dto.response;

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
}
