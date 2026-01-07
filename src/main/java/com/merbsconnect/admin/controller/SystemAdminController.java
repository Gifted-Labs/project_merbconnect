package com.merbsconnect.admin.controller;

import com.merbsconnect.admin.dto.response.SystemStatsResponse;
import com.merbsconnect.admin.service.SystemMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for system monitoring and administration.
 * All endpoints require SUPER_ADMIN or ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:5173" }, allowedHeaders = { "*" }, maxAge = 3600)
public class SystemAdminController {

    private final SystemMonitoringService systemMonitoringService;

    /**
     * Get comprehensive system statistics.
     * Requires SUPER_ADMIN or ADMIN role.
     *
     * @return System statistics response
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<SystemStatsResponse> getSystemStats() {
        try {
            log.info("Fetching system statistics");
            SystemStatsResponse stats = systemMonitoringService.getSystemStats();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching system stats: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get recent application logs.
     * Requires SUPER_ADMIN or ADMIN role.
     *
     * @param limit Maximum number of log entries (default: 100)
     * @return List of recent log entries
     */
    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<String>> getRecentLogs(
            @RequestParam(defaultValue = "100") int limit) {
        try {
            log.info("Fetching {} recent log entries", limit);
            List<String> logs = systemMonitoringService.getRecentLogs(limit);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching logs: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
