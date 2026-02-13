package com.merbsconnect.admin.controller;

import com.merbsconnect.admin.dto.request.UpdateConfigRequest;
import com.merbsconnect.admin.dto.response.ConfigResponse;
import com.merbsconnect.admin.service.ConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for system configuration management.
 * All endpoints require SUPER_ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:5173" }, allowedHeaders = { "*" }, maxAge = 3600)
public class ConfigController {

    private final ConfigService configService;

    /**
     * Get all configuration entries.
     * Requires SUPER_ADMIN role.
     *
     * @return List of all configurations
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ConfigResponse>> getAllConfigs() {
        try {
            log.info("Fetching all configuration entries");
            List<ConfigResponse> configs = configService.getAllConfigs();
            return new ResponseEntity<>(configs, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching configurations: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a single configuration entry by key.
     * Requires SUPER_ADMIN role.
     *
     * @param key Configuration key
     * @return Configuration entry
     */
    @GetMapping("/{key}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ConfigResponse> getConfigByKey(@PathVariable String key) {
        return configService.getConfig(key)
                .map(config -> new ResponseEntity<>(config, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Update or create a configuration entry.
     * Requires SUPER_ADMIN role.
     *
     * @param request Configuration update request
     * @return Updated configuration
     */
    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ConfigResponse> updateConfig(@Valid @RequestBody UpdateConfigRequest request) {
        try {
            log.info("Updating configuration for key: {}", request.getConfigKey());
            ConfigResponse config = configService.updateConfig(request);
            return new ResponseEntity<>(config, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating configuration: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
