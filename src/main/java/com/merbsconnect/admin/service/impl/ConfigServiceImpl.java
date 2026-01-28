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

        String currentUser = getCurrentUsername();

        SystemConfig config = configRepository.findByConfigKey(request.getConfigKey())
                .orElse(SystemConfig.builder()
                        .configKey(request.getConfigKey())
                        .build());

        config.setConfigValue(request.getConfigValue());
        config.setDescription(request.getDescription());
        config.setUpdatedBy(currentUser);

        SystemConfig savedConfig = configRepository.save(config);

        // Log audit trail
        auditService.logAction("UPDATE", "SystemConfig", savedConfig.getId(),
                "Updated config: " + savedConfig.getConfigKey());

        log.info("Successfully updated configuration for key: {}", request.getConfigKey());
        return mapToConfigResponse(savedConfig);
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
            log.warn("Could not get current username: {}", e.getMessage());
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
                .updatedAt(config.getUpdatedAt())
                .updatedBy(config.getUpdatedBy())
                .build();
    }
}
