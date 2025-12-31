package com.merbsconnect.authentication.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT authentication response containing tokens and user information")
public class JwtResponse {

    @Schema(
            description = "JWT access token for authenticating subsequent API requests",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;

    @Schema(
            description = "Token type (always 'Bearer')",
            example = "Bearer"
    )
    private String type = "Bearer";

    @Schema(
            description = "Refresh token for obtaining a new access token when the current one expires",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;

    @Schema(
            description = "Unique identifier of the authenticated user",
            example = "123"
    )
    private Long id;

    @Schema(
            description = "Username of the authenticated user",
            example = "john.doe@example.com"
    )
    private String username;

    @Schema(
            description = "List of roles assigned to the user",
            example = "[\"ROLE_USER\"]"
    )
    private List<String> roles;
}
