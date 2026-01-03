package com.merbsconnect.authentication.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Schema(description = "Password reset request payload")
public record PasswordResetRequest(
        @NotBlank(message = "Token is required")
        @Schema(
                description = "Password reset token received via email",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "Password must contain at least one digit, one lowercase, one uppercase letter, one special character, and no whitespace")
        @Schema(
                description = "New password. Must contain at least 8 characters with uppercase, lowercase, digit, and special character",
                example = "NewPass123!",
                minLength = 8
        )
        String newPassword,

        @NotBlank(message = "Password confirmation is required")
        @Schema(
                description = "Password confirmation - must match newPassword",
                example = "NewPass123!"
        )
        String confirmPassword
) {
}
