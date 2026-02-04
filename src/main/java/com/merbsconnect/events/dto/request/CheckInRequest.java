package com.merbsconnect.events.dto.request;

import com.merbsconnect.enums.CheckInMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for event check-in via QR code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInRequest {

    @NotBlank(message = "Registration token is required")
    private String registrationToken; // Token from QR code

    /**
     * Method of check-in. Defaults to MANUAL for backward compatibility.
     */
    @Builder.Default
    private CheckInMethod method = CheckInMethod.MANUAL;
}
