package com.merbsconnect.config;

import com.merbsconnect.authentication.domain.TokenType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.token.expiry")
@Data
public class TokenExpiryConfig {
    private int verificationHours;
    private int passwordResetHours;

    public Duration getExpiryDuration(TokenType tokenType) {
        return switch (tokenType) {
            case VERIFICATION -> Duration.ofHours(verificationHours);
            case PASSWORD_RESET -> Duration.ofHours(passwordResetHours);
            default -> throw new IllegalArgumentException("Unknown token type: " + tokenType);
        };
    }
}
