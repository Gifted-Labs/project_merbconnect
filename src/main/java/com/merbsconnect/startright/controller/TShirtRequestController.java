package com.merbsconnect.startright.controller;

import com.merbsconnect.startright.dto.request.TShirtRequestDto;
import com.merbsconnect.startright.dto.response.TShirtDailyAnalyticsDto;
import com.merbsconnect.startright.dto.response.TShirtDashboardDto;
import com.merbsconnect.startright.dto.response.TShirtRequestResponseDto;
import com.merbsconnect.startright.enums.RequestStatus;
import com.merbsconnect.startright.service.TShirtRequestService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tshirt-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TShirtRequestController {

    private final TShirtRequestService service;

    /**
     * Submit a new T-shirt request (PUBLIC - no auth required)
     */
    @PostMapping
    public ResponseEntity<TShirtRequestResponseDto> submitRequest(
            @Valid @RequestBody TShirtRequestDto requestDto) {
        TShirtRequestResponseDto response = service.submitRequest(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all T-shirt requests (ADMIN only)
     * Optional query param: status (PENDING, COMPLETED, DECLINED)
     */
    @GetMapping
    public ResponseEntity<List<TShirtRequestResponseDto>> getAllRequests(
            @RequestParam(required = false) RequestStatus status) {
        List<TShirtRequestResponseDto> requests = service.getAllRequests(status);
        return ResponseEntity.ok(requests);
    }

    /**
     * Get a specific request by ID (ADMIN only)
     */
    @GetMapping("/{id}")
    public ResponseEntity<TShirtRequestResponseDto> getRequestById(@PathVariable Long id) {
        TShirtRequestResponseDto response = service.getRequestById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update request status (ADMIN only)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TShirtRequestResponseDto> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        RequestStatus newStatus = RequestStatus.valueOf(statusUpdate.get("status"));
        TShirtRequestResponseDto response = service.updateRequestStatus(id, newStatus);
        return ResponseEntity.ok(response);
    }

    /**
     * Export all T-shirt requests as CSV (ADMIN only)
     */
    @GetMapping("/export")
    public void exportRequestsToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=tshirt_requests.csv");

        List<TShirtRequestResponseDto> requests = service.getAllRequestsForExport();

        PrintWriter writer = response.getWriter();

        // Write CSV header
        writer.println("Request ID,Full Name,Email,Phone Number,T-Shirt Color,T-Shirt Size,Quantity,Status,Created At");

        // Write data rows
        for (TShirtRequestResponseDto request : requests) {
            writer.println(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\"",
                    request.getRequestId(),
                    escapeCSV(request.getFullName()),
                    escapeCSV(request.getEmail()),
                    escapeCSV(request.getPhoneNumber()),
                    request.getTShirtColor(),
                    request.getTShirtSize(),
                    request.getQuantity(),
                    request.getRequestStatus().name(),
                    request.getCreatedAt().toString()));
        }

        writer.flush();
    }

    /**
     * Get daily analytics (ADMIN only)
     */
    @GetMapping("/analytics/daily")
    public ResponseEntity<List<TShirtDailyAnalyticsDto>> getDailyAnalytics() {
        List<TShirtDailyAnalyticsDto> analytics = service.getDailyAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get dashboard metrics (ADMIN only)
     */
    @GetMapping("/dashboard")
    public ResponseEntity<TShirtDashboardDto> getDashboardMetrics() {
        TShirtDashboardDto dashboard = service.getDashboardMetrics();
        return ResponseEntity.ok(dashboard);
    }

    private String escapeCSV(String value) {
        if (value == null)
            return "";
        return value.replace("\"", "\"\"");
    }
}
