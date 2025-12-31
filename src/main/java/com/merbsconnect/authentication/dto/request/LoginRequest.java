package com.merbsconnect.authentication.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request payload")
public class LoginRequest {

    @NotBlank
    @Schema(
            description = "User's email address",
            example = "john.doe@example.com",
            format = "email"
    )
    private String email;

    @NotBlank
    @Schema(
            description = "User's password",
            example = "SecurePass123!"
    )
    private String password;
}
