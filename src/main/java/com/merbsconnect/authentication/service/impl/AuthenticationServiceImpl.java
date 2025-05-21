package com.merbsconnect.authentication.service.impl;

import com.merbsconnect.authentication.domain.RefreshToken;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.dto.request.LoginRequest;
import com.merbsconnect.authentication.dto.request.PasswordResetRequest;
import com.merbsconnect.authentication.dto.request.RegistrationRequest;
import com.merbsconnect.authentication.dto.request.TokenRefreshRequest;
import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.authentication.security.CustomUserDetails;
import com.merbsconnect.authentication.security.jwt.JwtService;
import com.merbsconnect.authentication.service.AuthenticationService;
import com.merbsconnect.authentication.service.RefreshTokenService;
import com.merbsconnect.enums.UserRole;
import com.merbsconnect.exception.InvalidTokenException;
import com.merbsconnect.exception.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;


    @Override
    @Transactional
    public MessageResponse registerUser(RegistrationRequest request) {

        // Check if the user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new MessageResponse("User already exists with this email");
        }

        // Check if the phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return new MessageResponse("User already exists with this phone number");
        }

        // Create a new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_ADMIN)
                .isEnabled(false)
                .build();

        String verificationToken = jwtService.generateVerificationToken(user.getEmail());

        // Save the user to the database
        userRepository.save(user);

        return new MessageResponse("User registered successfully! Verification token: " + verificationToken);
    }

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));


        log.info("I am done authenticating the user");

        if (authentication == null) {
            throw new UsernameNotFoundException("User not found with email/username: " + loginRequest.getEmail());
        }


        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateVerificationToken(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .roles(roles)
                .build();
    }

    @Override
    public MessageResponse verifyEmail(String token) {
        try {
            String email = jwtService.getUsernameFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Error: User is not found."));

            if(user.isEnabled()) {
                return new MessageResponse("Error: User is already verified.");
            }
            user.setEnabled(true);
            userRepository.save(user);

            return new MessageResponse("Account verified successfully");
        }
        catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Verification token has expired");
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid verification token");
        }
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
        // Generate a password reset token
        String passwordResetToken = jwtService.generateVerificationToken(user.getEmail());
        return new MessageResponse("Password reset token: " + passwordResetToken);
    }



    @Transactional
    public MessageResponse resetPassword(PasswordResetRequest passwordResetRequest) {

        // Check if password and confirm matches.
        if(!passwordResetRequest.newPassword().equals(passwordResetRequest.confirmPassword())  ) {
            return new MessageResponse("Error: Passwords do not match.");
        }

        try {
            String email = jwtService.getUsernameFromToken(passwordResetRequest.token());
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email/username: "+email));

            user.setPassword(passwordEncoder.encode(passwordResetRequest.newPassword()));
            userRepository.save(user);

            return new MessageResponse("Password reset successfully.");
        }
        catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Password reset token has expired");
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid password reset token");
        }
    }

    @Override
    public JwtResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateTokenFromEmail(user.getEmail(), user.getRole().toString());
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

}
