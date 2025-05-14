package com.merbsconnect.authentication.controller;


import com.merbsconnect.authentication.dto.request.LoginRequest;
import com.merbsconnect.authentication.dto.request.PasswordResetRequest;
import com.merbsconnect.authentication.dto.request.RegistrationRequest;
import com.merbsconnect.authentication.dto.request.TokenRefreshRequest;
import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.authentication.service.AuthenticationService;
import com.merbsconnect.authentication.service.impl.AuthenticationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest) {
        MessageResponse messageResponse = authenticationService.registerUser(registrationRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authenticationService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }


    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam("token") String token) {
        MessageResponse messageResponse = authenticationService.verifyEmail(token);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestParam("email") String email) {
        MessageResponse messageResponse = authenticationService.requestPasswordReset(email);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody PasswordResetRequest passwordResetRequest
    ) {
        MessageResponse messageResponse = authenticationService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(
            @RequestParam TokenRefreshRequest refreshTokenRequest
    ) {
        JwtResponse jwtResponse = authenticationService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(jwtResponse);
    }





}
