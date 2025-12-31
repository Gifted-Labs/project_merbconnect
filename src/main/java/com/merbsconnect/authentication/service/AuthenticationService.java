package com.merbsconnect.authentication.service;

import com.merbsconnect.authentication.dto.request.LoginRequest;
import com.merbsconnect.authentication.dto.request.PasswordResetRequest;
import com.merbsconnect.authentication.dto.request.RegistrationRequest;
import com.merbsconnect.authentication.dto.request.TokenRefreshRequest;
import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;


public interface AuthenticationService {

    MessageResponse registerUser(RegistrationRequest request);

    JwtResponse authenticateUser(LoginRequest request);

    MessageResponse verifyEmail(String token);

    MessageResponse verifyPhoneNumber(String token);

    MessageResponse requestPasswordReset(String email);

    MessageResponse resetPassword(PasswordResetRequest request);

    JwtResponse refreshToken(TokenRefreshRequest request);

    JwtResponse resendVerificationToken(TokenRefreshRequest request);
}
