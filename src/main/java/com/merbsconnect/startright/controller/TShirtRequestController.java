package com.merbsconnect.startright.controller;

import com.merbsconnect.startright.dto.request.TShirtRequestDto;
import com.merbsconnect.startright.dto.response.TShirtRequestResponseDto;
import com.merbsconnect.startright.enums.RequestStatus;
import com.merbsconnect.startright.service.TShirtRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tshirt-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TShirtRequestController {

    private final TShirtRequestService service;

    /**
     * Submit a new T-shirt request
     */
    @PostMapping
    public ResponseEntity<TShirtRequestResponseDto> submitRequest(
            @Valid @RequestBody TShirtRequestDto requestDto) {
        TShirtRequestResponseDto response = service.submitRequest(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all T-shirt requests (Admin)
     * Optional query param: status (PENDING, COMPLETED, DECLINED)
     */
    @GetMapping
    public ResponseEntity<List<TShirtRequestResponseDto>> getAllRequests(
            @RequestParam(required = false) RequestStatus status) {
        List<TShirtRequestResponseDto> requests = service.getAllRequests(status);
        return ResponseEntity.ok(requests);
    }

    /**
     * Get a specific request by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TShirtRequestResponseDto> getRequestById(@PathVariable Long id) {
        TShirtRequestResponseDto response = service.getRequestById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update request status (Admin)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TShirtRequestResponseDto> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        RequestStatus newStatus = RequestStatus.valueOf(statusUpdate.get("status"));
        TShirtRequestResponseDto response = service.updateRequestStatus(id, newStatus);
        return ResponseEntity.ok(response);
    }
}
