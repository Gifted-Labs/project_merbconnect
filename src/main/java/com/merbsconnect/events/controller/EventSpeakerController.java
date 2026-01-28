package com.merbsconnect.events.controller;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.EventSpeakerRequest;
import com.merbsconnect.events.dto.response.EventSpeakerResponse;
import com.merbsconnect.events.service.EventSpeakerService;
import com.merbsconnect.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for managing event speakers with S3 image upload support.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events/{eventId}/speakers-v2")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:5173", "http://localhost:5175" }, allowedHeaders = {
        "*" }, maxAge = 3600)
@Tag(name = "Event Speakers V2", description = "Enhanced speaker management with S3 image upload")
public class EventSpeakerController {

    private final EventSpeakerService speakerService;

    @Operation(summary = "Add a new speaker to an event")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<EventSpeakerResponse> addSpeaker(
            @PathVariable Long eventId,
            @RequestBody EventSpeakerRequest request) {
        try {
            log.info("Adding speaker to event ID: {}", eventId);
            EventSpeakerResponse response = speakerService.addSpeaker(eventId, request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BusinessException e) {
            log.error("Error adding speaker: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update an existing speaker")
    @PutMapping("/{speakerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<EventSpeakerResponse> updateSpeaker(
            @PathVariable Long eventId,
            @PathVariable Long speakerId,
            @RequestBody EventSpeakerRequest request) {
        try {
            log.info("Updating speaker ID {} for event ID: {}", speakerId, eventId);
            EventSpeakerResponse response = speakerService.updateSpeaker(eventId, speakerId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error updating speaker: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a speaker from an event")
    @DeleteMapping("/{speakerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MessageResponse> deleteSpeaker(
            @PathVariable Long eventId,
            @PathVariable Long speakerId) {
        try {
            log.info("Deleting speaker ID {} from event ID: {}", speakerId, eventId);
            MessageResponse response = speakerService.deleteSpeaker(eventId, speakerId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error deleting speaker: {}", e.getMessage());
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all speakers for an event")
    @GetMapping
    public ResponseEntity<List<EventSpeakerResponse>> getSpeakers(@PathVariable Long eventId) {
        try {
            log.info("Fetching speakers for event ID: {}", eventId);
            List<EventSpeakerResponse> speakers = speakerService.getSpeakers(eventId);
            return new ResponseEntity<>(speakers, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error fetching speakers: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get a specific speaker by ID")
    @GetMapping("/{speakerId}")
    public ResponseEntity<EventSpeakerResponse> getSpeakerById(
            @PathVariable Long eventId,
            @PathVariable Long speakerId) {
        try {
            log.info("Fetching speaker ID {} for event ID: {}", speakerId, eventId);
            EventSpeakerResponse response = speakerService.getSpeakerById(eventId, speakerId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error fetching speaker: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Upload an image for a speaker to S3")
    @PostMapping(value = "/{speakerId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<EventSpeakerResponse> uploadSpeakerImage(
            @PathVariable Long eventId,
            @PathVariable Long speakerId,
            @RequestParam("file") MultipartFile image) {
        try {
            log.info("Uploading image for speaker ID {} in event ID: {}", speakerId, eventId);
            EventSpeakerResponse response = speakerService.uploadSpeakerImage(eventId, speakerId, image);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error uploading speaker image: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.error("IO error uploading speaker image: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete a speaker's image from S3")
    @DeleteMapping("/{speakerId}/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<MessageResponse> deleteSpeakerImage(
            @PathVariable Long eventId,
            @PathVariable Long speakerId) {
        try {
            log.info("Deleting image for speaker ID {} in event ID: {}", speakerId, eventId);
            MessageResponse response = speakerService.deleteSpeakerImage(eventId, speakerId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error deleting speaker image: {}", e.getMessage());
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
