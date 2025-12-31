package com.merbsconnect.util;

import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.authentication.repository.VerificationTokenRepository;
import com.merbsconnect.config.TokenExpiryConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenGenerator {

    private final VerificationTokenRepository tokenRepository;
    private final TokenExpiryConfig expiryConfig;

    public VerificationToken generate (User user, TokenType tokenType) {
        String tokenValue = generateSecureToken();
        Duration expiryDuration = expiryConfig.getExpiryDuration(tokenType);

        VerificationToken token = VerificationToken.create(
                user,
                tokenValue,
                tokenType,
                expiryDuration
        );

        return tokenRepository.save(token);
    }


    private String generateSecureToken(){
        return UUID.randomUUID().toString();
    }
}
