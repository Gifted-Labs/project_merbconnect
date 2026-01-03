package com.merbsconnect.util;

import com.merbsconnect.authentication.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {
    private final VerificationTokenRepository tokenRepository;

    @Scheduled(cron = "${app.token.cleanup.cron:0 0 0 * * ?}")
    public void cleanUpExpiredTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);

        int deleted = tokenRepository.deleteByExpiresAtBeforeOrVerifiedAtIsNotNullAndCreatedAtBefore(
                cutoff,
                cutoff
        );

        log.info("Token cleanup completed: {} expired or used tokens removed", deleted);
    }
}
