package com.merbsconnect.admin.controller;

import com.merbsconnect.admin.dto.response.AnalyticsResponse;
import com.merbsconnect.admin.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for analytics endpoints.
 * All endpoints require SUPER_ADMIN or ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:5173" }, allowedHeaders = { "*" }, maxAge = 3600)
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get comprehensive analytics data for the admin dashboard.
     * Provides all metrics in a single API call.
     *
     * @return AnalyticsResponse containing dashboard metrics
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        try {
            log.info("Fetching analytics data");
            AnalyticsResponse analytics = analyticsService.getAnalyticsData();
            return new ResponseEntity<>(analytics, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching analytics data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
