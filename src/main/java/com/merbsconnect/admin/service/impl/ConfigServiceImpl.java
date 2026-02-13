package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.request.UpdateConfigRequest;
import com.merbsconnect.admin.dto.response.ConfigResponse;
import com.merbsconnect.admin.model.SystemConfig;
import com.merbsconnect.admin.repository.SystemConfigRepository;
import com.merbsconnect.admin.service.AuditService;
import com.merbsconnect.admin.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ConfigService for configuration management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final SystemConfigRepository configRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public List<ConfigResponse> getAllConfigs() {
        log.info("Fetching all configuration entries");
        return configRepository.findAll().stream()
                .map(this::mapToConfigResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConfigResponse> getConfig(String key) {
        log.info("Fetching configuration for key: {}", key);
        return configRepository.findByConfigKey(key)
                .map(this::mapToConfigResponse);
    }

    @Override
    @Transactional
    public ConfigResponse updateConfig(UpdateConfigRequest request) {
        log.info("Updating configuration for key: {}", request.getConfigKey());

        // We can accept "System" as current user if not authenticated (e.g. startup)
        String currentUser = getCurrentUsername();

        SystemConfig config = configRepository.findByConfigKey(request.getConfigKey())
                .orElse(SystemConfig.builder()
                        .configKey(request.getConfigKey())
                        .build());

        config.setConfigValue(request.getConfigValue());
        if (request.getDescription() != null)
            config.setDescription(request.getDescription());
        if (request.getCategory() != null)
            config.setCategory(request.getCategory());
        if (request.getType() != null)
            config.setType(request.getType());

        config.setUpdatedBy(currentUser);

        SystemConfig savedConfig = configRepository.save(config);

        // Log audit trail
        auditService.logAction("UPDATE", "SystemConfig", savedConfig.getId(),
                "Updated config: " + savedConfig.getConfigKey());

        log.info("Successfully updated configuration for key: {}", request.getConfigKey());
        return mapToConfigResponse(savedConfig);
    }

    @jakarta.annotation.PostConstruct
    public void seedDefaultConfigs() {
        seedConfig("APP_NAME", "MerbsConnect", "General", "TEXT", "Application Name");
        seedConfig("APP_TAGLINE", "Connecting Communities", "General", "TEXT", "Application Tagline");
        seedConfig("CONTACT_EMAIL", "admin@merbsconnect.com", "General", "TEXT", "Contact Email");
        seedConfig("ADMIN_PHONE", "+1234567890", "General", "TEXT", "Admin Phone Number");

        seedConfig("SYSTEM_LOGO_LIGHT", "", "Branding", "FILE", "System Logo (Light Mode)");
        seedConfig("SYSTEM_LOGO_DARK", "", "Branding", "FILE", "System Logo (Dark Mode)");
        seedConfig("FAVICON", "", "Branding", "FILE", "Favicon");

        seedConfig("TIMEZONE", "UTC", "Localization", "LIST", "System Timezone");
        seedConfig("DATE_FORMAT", "DD/MM/YYYY", "Localization", "LIST", "Date Format");

        seedConfig("REGISTRATION_ENABLED", "true", "Events", "BOOLEAN", "Enable User Registration");
        seedConfig("CHECKIN_WINDOW_HOURS", "2", "Events", "NUMBER", "Check-in Window (Hours before start)");
        seedConfig("GUEST_ACCESS_ENABLED", "false", "Events", "BOOLEAN", "Allow Guest Access");

        seedConfig("SMTP_HOST", "", "Notifications", "TEXT", "SMTP Host");
        seedConfig("SMTP_PORT", "587", "Notifications", "NUMBER", "SMTP Port");
        seedConfig("SMTP_USER", "", "Notifications", "TEXT", "SMTP Username");
        seedConfig("SMTP_PASSWORD", "", "Notifications", "PASSWORD", "SMTP Password");
        seedConfig("TSHIRT_REQUEST_EMAIL", "merch@merbsconnect.com", "Notifications", "TEXT",
                "Email for T-Shirt Requests");

        seedConfig("MAINTENANCE_MODE", "false", "System", "BOOLEAN", "Maintenance Mode");
        seedConfig("SESSION_TIMEOUT_MINUTES", "30", "Security", "NUMBER", "Session Timeout (Minutes)");
        seedConfig("SESSION_TIMEOUT_MINUTES", "30", "Security", "NUMBER", "Session Timeout (Minutes)");
        seedConfig("REQUIRE_STRONG_PASSWORD", "true", "Security", "BOOLEAN", "Require Strong Passwords");

        seedConfig("QA_FEATURE_ENABLED", "true", "StartRight", "BOOLEAN", "Enable Live Q&A Feature");
    }

    private void seedConfig(String key, String value, String category, String type, String description) {
        if (!configRepository.existsByConfigKey(key)) {
            configRepository.save(SystemConfig.builder()
                    .configKey(key)
                    .configValue(value)
                    .category(category)
                    .type(type)
                    .description(description)
                    .updatedBy("SYSTEM")
                    .build());
            log.info("Seeded default config: {}", key);
        }
    }

    /**
     * Get current username from security context.
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // ignore
        }
        return "SYSTEM";
    }

    /**
     * Map SystemConfig entity to ConfigResponse DTO.
     */
    private ConfigResponse mapToConfigResponse(SystemConfig config) {
        return ConfigResponse.builder()
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .description(config.getDescription())
                .category(config.getCategory())
                .type(config.getType())
                .updatedAt(config.getUpdatedAt())
                .updatedBy(config.getUpdatedBy())
                .build();
    }
}
