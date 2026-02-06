package com.merbsconnect.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Temporary component to fix database schema database inconsistencies.
 * This can be removed after the fix is applied.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSchemaFix implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking/Applying database schema fixes...");
        try {
            // Fix for: null value in column "user_id" of relation "event_reviews" violates
            // not-null constraint
            // This allows guest reviews where user_id is null.
            String sql = "ALTER TABLE event_reviews ALTER COLUMN user_id DROP NOT NULL";
            jdbcTemplate.execute(sql);
            log.info("Successfully executed: {}", sql);
        } catch (Exception e) {
            // Use debug or warn to avoid alarming if it's already done or fails safely
            log.warn("Database fix might have already run or failed: {}", e.getMessage());
        }
    }
}
