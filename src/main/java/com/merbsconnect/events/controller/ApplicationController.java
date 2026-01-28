package com.merbsconnect.events.controller;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.SponsorApplicationRequest;
import com.merbsconnect.events.dto.request.VolunteerApplicationRequest;
import com.merbsconnect.events.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/volunteer")
    public ResponseEntity<MessageResponse> applyToVolunteer(@Valid @RequestBody VolunteerApplicationRequest request) {
        return ResponseEntity.ok(applicationService.applyToVolunteer(request));
    }

    @PostMapping("/sponsor")
    public ResponseEntity<MessageResponse> applyToSponsor(@Valid @RequestBody SponsorApplicationRequest request) {
        return ResponseEntity.ok(applicationService.applyToSponsor(request));
    }
}
