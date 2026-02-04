package com.merbsconnect.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * Configuration for async email sending with proper exception handling.
 */
@Slf4j
@Configuration
@EnableAsync
public class EmailConfig implements AsyncConfigurer {

    @Bean(name = "emailTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("EmailThread-");
        executor.initialize();
        return executor;
    }

    /**
     * Handles uncaught exceptions from @Async methods.
     * This is critical for debugging silent email failures.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("=== ASYNC EXCEPTION HANDLER ===");
            log.error("Exception in async method: {}", method.getName());
            log.error("Method parameters: {}", Arrays.toString(params));
            log.error("Exception message: {}", ex.getMessage());
            log.error("Exception class: {}", ex.getClass().getName());
            log.error("Full stack trace:", ex);
        };
    }
}