package com.merbsconnect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.token.rate-limit")
@Data
public class RateLimitConfig {
    private final int MAX_ATTEMPTS = 3;
    private final int COOL_DOWN_MINUTES = 1;
    private final int ATTEMPT_WINDOW_HOURS = 1;

    public Duration getCooldownDuration() {
        return Duration.ofMinutes(COOL_DOWN_MINUTES);
    }

    public Duration getAttemptWindowDuration() {
        return Duration.ofHours(ATTEMPT_WINDOW_HOURS);
    }
}

