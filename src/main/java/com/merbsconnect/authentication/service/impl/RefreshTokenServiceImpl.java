package com.merbsconnect.authentication.service.impl;

import com.merbsconnect.authentication.domain.RefreshToken;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.repository.RefreshTokenRepository;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.authentication.service.RefreshTokenService;
import com.merbsconnect.exception.TokenRefreshException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${app.jwt.refresh-token-expiration}")
    private Long refreshTokenDurationMs;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .token(UUID.randomUUID().toString())
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.isExpired()){
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),"Refresh token was expired. Please make a new sign-in request");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }


}
