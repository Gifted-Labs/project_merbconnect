package com.merbsconnect.authentication.controller;


import com.merbsconnect.authentication.dto.request.LoginRequest;
import com.merbsconnect.authentication.dto.request.PasswordResetRequest;
import com.merbsconnect.authentication.dto.request.RegistrationRequest;
import com.merbsconnect.authentication.dto.request.TokenRefreshRequest;
import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@Component("authenticationController")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174","http://localhost:8080"}, allowedHeaders = {"*"}, maxAge = 3600)
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

    // Thhis endpoint requests for full authentication before access so has to be fixed
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


    /* TODO: Done creating the api endpionts in this class so upnext, i have
        to fix the following errors.

        1. I have to fix the error in the full authentication required and
            structure the generated token well.
         2. Create a function users can use to resend verification tokens and also password reset tokens
        3. Proper error handling and finish off the authentication.
    * */


}
