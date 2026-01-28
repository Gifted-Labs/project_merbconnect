package com.merbsconnect.admin.service;

import com.merbsconnect.admin.dto.request.UpdateConfigRequest;
import com.merbsconnect.admin.dto.response.ConfigResponse;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for system configuration management.
 */
public interface ConfigService {

    /**
     * Get all configuration entries.
     *
     * @return List of configuration responses
     */
    List<ConfigResponse> getAllConfigs();

    /**
     * Get configuration by key.
     *
     * @param key Configuration key
     * @return Optional configuration response
     */
    Optional<ConfigResponse> getConfig(String key);

    /**
     * Update or create a configuration entry.
     *
     * @param request Configuration update request
     * @return Updated configuration response
     */
    ConfigResponse updateConfig(UpdateConfigRequest request);
}
