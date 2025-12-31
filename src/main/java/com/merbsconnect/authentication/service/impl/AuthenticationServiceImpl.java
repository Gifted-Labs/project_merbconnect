package com.merbsconnect.authentication.service.impl;

import com.merbsconnect.academics.util.ResourceMapper;
import com.merbsconnect.authentication.domain.RefreshToken;
import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.authentication.dto.request.LoginRequest;
import com.merbsconnect.authentication.dto.request.PasswordResetRequest;
import com.merbsconnect.authentication.dto.request.RegistrationRequest;
import com.merbsconnect.authentication.dto.request.TokenRefreshRequest;
import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.authentication.repository.VerificationTokenRepository;
import com.merbsconnect.authentication.security.CustomUserDetails;
import com.merbsconnect.authentication.security.jwt.JwtService;
import com.merbsconnect.authentication.service.AuthenticationService;
import com.merbsconnect.authentication.service.RefreshTokenService;
import com.merbsconnect.authentication.service.TokenService;
import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.enums.UserRole;
import com.merbsconnect.exception.UnauthorizedException;
import com.merbsconnect.util.TokenResendFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final VerificationTokenRepository tokenRepository;
    private final TokenResendFactory tokenResendFactory;

    @Override
    @Transactional
    public MessageResponse registerUser(RegistrationRequest request) throws BadRequestException {
        validateRegistrationRequest(request);


        User user = createUserFromRequest(request);
        User savedUser = userRepository.save(user);
        
        sendVerificationEmail(savedUser);

        return new MessageResponse("User registered successfully! Please check your email to verify your account.");
    }

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticateCredentials(loginRequest);


        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        validateUserStatus(userDetails);

        return generateJwtResponse(authentication, userDetails);
    }

    @Override
    public MessageResponse verifyEmail(String tokenValue) {
        VerificationToken token = tokenService.validateToken(
                tokenValue, TokenType.VERIFICATION);
        User user = token.getUser();

            if(user.isEnabled()) {
                return new MessageResponse("Account is already verified.");
            }

            verifyUserEmail(user, token);
            return new MessageResponse("Account verified successfully");
        }


    @Override
    public MessageResponse verifyPhoneNumber(String token) {
        return null;
    }

    @Override
    public MessageResponse requestPasswordReset(String email) {
        // Check if the user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
                
        VerificationToken passwordResetToken = tokenService.generatePasswordResetToken(user);
        // Send password reset email
        emailService.sendPasswordResetEmail(user, passwordResetToken);
        
        return new MessageResponse("Password reset instructions have been sent to your email.");
    }

    @Transactional
    public MessageResponse resetPassword(PasswordResetRequest passwordResetRequest) throws BadRequestException {

        validatePasswordMatch(passwordResetRequest);



        VerificationToken token = tokenService.validateToken(
                passwordResetRequest.token(), TokenType.PASSWORD_RESET);
        User user = token.getUser();

        updateUserPassword(user, passwordResetRequest.newPassword());
        markTokenAsUsed(token);

        return new MessageResponse("Password has been reset successfully.");
    }

    private void validatePasswordMatch(PasswordResetRequest request) throws BadRequestException {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
    }

    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void markTokenAsUsed(VerificationToken token) {
        token.markAsUsed();
        tokenRepository.save(token);
    }

    @Override

    public JwtResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateTokenFromEmail(user.getEmail());
                    return JwtResponse.builder()
                            .token(accessToken)
                            .refreshToken(refreshToken)
                            .id(user.getId())
                            .roles(List.of(user.getRole().toString()))
                            .build();
                }).orElseThrow(() -> {
                    log.error("Refresh token is not in database!");
                    return new RuntimeException("Refresh token is not in the database!");
                });
    }

    @Override
    public MessageResponse resendToken(String email, TokenType tokenType) throws BadRequestException {
        tokenResendFactory.resendToken(email, tokenType);
        return new MessageResponse(tokenType + " token resent successfully");
    }


    private void validateRegistrationRequest(RegistrationRequest request) throws BadRequestException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number is already in use");
        }
    }

    private User createUserFromRequest(RegistrationRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_USER)
                .isEnabled(false)
                .build();
    }

    private void sendVerificationEmail(User user) {
        VerificationToken verificationToken = tokenService.generateVerificationToken(user);
        emailService.sendVerificationEmail(user, verificationToken.getToken());
        log.info("Sent verification email to {}", user.getEmail());
    }


    private Authentication authenticateCredentials(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    private void validateUserStatus(CustomUserDetails user) {
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is not verified. Please verify your email first.");
        }
        // Additional status checks can be added here (locked, expired, etc.)
    }

    private JwtResponse generateJwtResponse(Authentication authentication, CustomUserDetails userDetails) {
        String accessToken = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateTokenFromEmail(userDetails.getUsername());

        return ResourceMapper.toJwtResponse(
                userDetails,
                accessToken,
                refreshToken
        );
    }


    private void verifyUserEmail(User user, VerificationToken token) {
        user.setEnabled(true);
        userRepository.save(user);

        token.markAsUsed();
        tokenRepository.save(token);
    }


}
