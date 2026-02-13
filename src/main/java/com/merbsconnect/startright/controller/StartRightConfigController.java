package com.merbsconnect.startright.controller;

import com.merbsconnect.admin.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/startright/config")
@RequiredArgsConstructor
public class StartRightConfigController {

    private final ConfigService configService;

    @GetMapping("/session-status")
    public ResponseEntity<Map<String, Boolean>> getSessionStatus() {
        boolean isActive = configService.getConfig("STARTRIGHT_SESSION_ACTIVE")
                .map(config -> Boolean.parseBoolean(config.getConfigValue()))
                .orElse(true); // Default to true if config missing

        return ResponseEntity.ok(Map.of("active", isActive));
    }
}
