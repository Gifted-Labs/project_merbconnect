package com.merbsconnect.authentication.service;

import com.merbsconnect.authentication.dto.*;


public interface AuthenticationService {

    MessageResponse registerUser(RegistrationRequest request);

    JwtResponse authenticateUser(LoginRequest request);

    MessageResponse verifyEmail(String token);

    MessageResponse verifyPhoneNumber(String token);

    MessageResponse requestPasswordReset(String email);


    MessageResponse resetPassword(PasswordResetRequest request);

    JwtResponse refreshToken(TokenRefreshRequest request);
}
