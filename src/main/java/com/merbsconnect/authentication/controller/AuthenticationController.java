package com.merbsconnect.authentication.controller;


import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.dto.request.LoginRequest;
import com.merbsconnect.authentication.dto.request.PasswordResetRequest;
import com.merbsconnect.authentication.dto.request.RegistrationRequest;
import com.merbsconnect.authentication.dto.request.TokenRefreshRequest;
import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.authentication.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@Component("authenticationController")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174","http://localhost:8080"}, allowedHeaders = {"*"}, maxAge = 3600)
@Tag(name = "Authentication", description = "Authentication API endpoints for user registration, login, email verification, password reset, token refresh, and token resend operations")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided registration details. " +
                    "The user email must be unique and valid. A verification email will be sent to the provided email address.",
            tags = {"User Registration"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully. Please check your email for verification link.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - validation failed. Possible reasons:\n" +
                            "- First name or last name is blank or exceeds 50 characters\n" +
                            "- Email format is invalid\n" +
                            "- Email already exists\n" +
                            "- Password does not meet security requirements (minimum 8 characters, " +
                            "must contain uppercase, lowercase, digit, and special character)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while processing registration",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<MessageResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest) throws BadRequestException {
        MessageResponse messageResponse = authenticationService.registerUser(registrationRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user and generate JWT tokens",
            description = "Authenticates a user with their email and password credentials. " +
                    "On successful authentication, returns a JWT access token, refresh token, and user details. " +
                    "The access token should be used in the Authorization header for subsequent API requests.",
            tags = {"User Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful. Returns JWT access token, refresh token, and user information.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - email or password is blank",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials - email does not exist or password is incorrect",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Email not verified. Please verify your email before logging in.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during authentication",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<JwtResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authenticationService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }


    @GetMapping("/verify-email")
    @Operation(
            summary = "Verify user email address",
            description = "Verifies the user's email address using the verification token sent to their email. " +
                    "The token is typically provided as a link in the verification email. " +
                    "Email verification must be completed before the user can log in.",
            tags = {"Email Verification"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully. User can now log in.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired verification token",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User associated with the token not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during email verification",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<MessageResponse> verifyEmail(
            @Parameter(
                    name = "token",
                    description = "Email verification token sent to user's email address",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestParam("token") String token) {
        MessageResponse messageResponse = authenticationService.verifyEmail(token);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request a password reset",
            description = "Initiates a password reset process for a user account. " +
                    "A password reset link will be sent to the provided email address. " +
                    "This endpoint does not require authentication and can be accessed without a valid JWT token.",
            tags = {"Password Reset"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset email sent successfully. Please check your email for the reset link.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email format or email parameter is missing",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User with the provided email address not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while sending password reset email",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<MessageResponse> forgotPassword(
            @Parameter(
                    name = "email",
                    description = "Email address of the user requesting password reset",
                    required = true,
                    example = "user@example.com"
            )
            @RequestParam("email") String email) {
        MessageResponse messageResponse = authenticationService.requestPasswordReset(email);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset user password with verification token",
            description = "Resets a user's password using a valid password reset token. " +
                    "The new password must meet the security requirements (minimum 8 characters, " +
                    "must contain uppercase, lowercase, digit, and special character). " +
                    "This endpoint does not require JWT authentication.",
            tags = {"Password Reset"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully. You can now log in with your new password.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - validation failed. Possible reasons:\n" +
                            "- Token is blank or invalid\n" +
                            "- New password is blank\n" +
                            "- Password does not meet security requirements\n" +
                            "- Password confirmation does not match new password\n" +
                            "- Token has expired",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User associated with the password reset token not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while resetting password",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody PasswordResetRequest passwordResetRequest
    ) throws BadRequestException {
        MessageResponse messageResponse = authenticationService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh JWT access token",
            description = "Generates a new JWT access token using a valid refresh token. " +
                    "Use this endpoint when your access token has expired but your refresh token is still valid. " +
                    "The new access token can be used for subsequent API requests.",
            tags = {"Token Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token refreshed successfully. Use the new token for API requests.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or missing refresh token",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token has expired or is invalid",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while refreshing token",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<JwtResponse> refreshToken(
            @Valid @RequestBody TokenRefreshRequest refreshTokenRequest
    ) {
        JwtResponse jwtResponse = authenticationService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/resend-token")
    @Operation(
            summary = "Resend verification or password reset token",
            description = "Resends a verification email or password reset email to the user. " +
                    "Use this endpoint if the user did not receive the initial token email or if the token has expired. " +
                    "Specify the token type (VERIFICATION or PASSWORD_RESET) to indicate which token to resend.",
            tags = {"Token Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token resent successfully. Please check your email.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - email format is invalid or token type is missing",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User with the provided email address not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while resending token",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<MessageResponse> resendToken(
            @Parameter(
                    name = "email",
                    description = "Email address of the user requesting token resend",
                    required = true,
                    example = "user@example.com"
            )
            @RequestParam String email,
            @Parameter(
                    name = "tokenType",
                    description = "Type of token to resend (VERIFICATION or PASSWORD_RESET)",
                    required = true,
                    example = "VERIFICATION"
            )
            @RequestParam TokenType tokenType
    ) throws BadRequestException {
        MessageResponse response = authenticationService.resendToken(email, tokenType);
        return ResponseEntity.ok(response);
    }

}
