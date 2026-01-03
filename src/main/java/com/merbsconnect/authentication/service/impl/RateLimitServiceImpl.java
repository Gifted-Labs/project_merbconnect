package com.merbsconnect.authentication.service.impl;

import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.authentication.repository.VerificationTokenRepository;
import com.merbsconnect.authentication.service.RateLimitService;
import com.merbsconnect.config.RateLimitConfig;
import com.merbsconnect.exception.RateLimitException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    private final VerificationTokenRepository tokenRepository;
    private final RateLimitConfig rateLimitConfig;

    @Override
    public void checkRateLimit(User user, TokenType tokenType) {
        checkCooldownPeriod(user, tokenType);
        checkMaxAttempts(user, tokenType);
    }

    private void checkCooldownPeriod(User user, TokenType tokenType) {
        LocalDateTime cooldownThreshold = LocalDateTime
                .now().minus(rateLimitConfig.getCooldownDuration());

        List<VerificationToken> recentAttempts = tokenRepository
                .findAllByUserAndTokenTypeAndCreatedAtAfter(
                        user,
                        tokenType,
                        cooldownThreshold
                );
        if (!recentAttempts.isEmpty()){
            throw new RateLimitException(
                    "Please, wait before requesting another verification email"
            );
        }
    }

    private void checkMaxAttempts(User user, TokenType tokenType) {
        LocalDateTime windowStart = LocalDateTime
                .now().minus(rateLimitConfig.getAttemptWindowDuration());

        long recentCount = tokenRepository.countByUserAndTokenTypeAndCreatedAtAfter(
                user,
                tokenType,
                windowStart
        );

        if (recentCount >= rateLimitConfig.getMAX_ATTEMPTS()) {
            throw new RateLimitException(
                    "Maximum resend attempts exceeded. Please try again later."
            );
        }
    }
}
