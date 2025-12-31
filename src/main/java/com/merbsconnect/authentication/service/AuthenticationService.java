package com.merbsconnect.authentication.service;

import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.dto.request.LoginRequest;
import com.merbsconnect.authentication.dto.request.PasswordResetRequest;
import com.merbsconnect.authentication.dto.request.RegistrationRequest;
import com.merbsconnect.authentication.dto.request.TokenRefreshRequest;
import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import org.apache.coyote.BadRequestException;


public interface AuthenticationService {

    MessageResponse registerUser(RegistrationRequest request) throws BadRequestException;

    JwtResponse authenticateUser(LoginRequest request);

    MessageResponse verifyEmail(String token);

    MessageResponse verifyPhoneNumber(String token);

    MessageResponse requestPasswordReset(String email);

    MessageResponse resetPassword(PasswordResetRequest request) throws BadRequestException;

    JwtResponse refreshToken(TokenRefreshRequest request);

    MessageResponse resendToken(String email, TokenType tokenType) throws BadRequestException;
}
