package com.merbsconnect.events.controller;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.EventItineraryItemRequest;
import com.merbsconnect.events.dto.response.EventItineraryItemResponse;
import com.merbsconnect.events.service.EventItineraryService;
import com.merbsconnect.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing event itinerary/program lineup.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events/{eventId}/itinerary")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:5173", "http://localhost:5175" }, allowedHeaders = {
        "*" }, maxAge = 3600)
@Tag(name = "Event Itinerary", description = "Manage event program lineup and activities")
public class EventItineraryController {

    private final EventItineraryService itineraryService;

    @Operation(summary = "Add a new itinerary item to an event")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<EventItineraryItemResponse> addItineraryItem(
            @PathVariable Long eventId,
            @RequestBody EventItineraryItemRequest request) {
        try {
            log.info("Adding itinerary item to event ID: {}", eventId);
            EventItineraryItemResponse response = itineraryService.addItineraryItem(eventId, request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BusinessException e) {
            log.error("Error adding itinerary item: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Bulk add multiple itinerary items to an event")
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<List<EventItineraryItemResponse>> bulkAddItineraryItems(
            @PathVariable Long eventId,
            @RequestBody List<EventItineraryItemRequest> requests) {
        try {
            log.info("Bulk adding {} itinerary items to event ID: {}", requests.size(), eventId);
            List<EventItineraryItemResponse> responses = itineraryService.bulkAddItineraryItems(eventId, requests);
            return new ResponseEntity<>(responses, HttpStatus.CREATED);
        } catch (BusinessException e) {
            log.error("Error bulk adding itinerary items: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update an existing itinerary item")
    @PutMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<EventItineraryItemResponse> updateItineraryItem(
            @PathVariable Long eventId,
            @PathVariable Long itemId,
            @RequestBody EventItineraryItemRequest request) {
        try {
            log.info("Updating itinerary item ID {} for event ID: {}", itemId, eventId);
            EventItineraryItemResponse response = itineraryService.updateItineraryItem(eventId, itemId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error updating itinerary item: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete an itinerary item from an event")
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MessageResponse> deleteItineraryItem(
            @PathVariable Long eventId,
            @PathVariable Long itemId) {
        try {
            log.info("Deleting itinerary item ID {} from event ID: {}", itemId, eventId);
            MessageResponse response = itineraryService.deleteItineraryItem(eventId, itemId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error deleting itinerary item: {}", e.getMessage());
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all itinerary items for an event (ordered by display order/time)")
    @GetMapping
    public ResponseEntity<List<EventItineraryItemResponse>> getItinerary(@PathVariable Long eventId) {
        try {
            log.info("Fetching itinerary for event ID: {}", eventId);
            List<EventItineraryItemResponse> itinerary = itineraryService.getItinerary(eventId);
            return new ResponseEntity<>(itinerary, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error fetching itinerary: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get a specific itinerary item by ID")
    @GetMapping("/{itemId}")
    public ResponseEntity<EventItineraryItemResponse> getItineraryItemById(
            @PathVariable Long eventId,
            @PathVariable Long itemId) {
        try {
            log.info("Fetching itinerary item ID {} for event ID: {}", itemId, eventId);
            EventItineraryItemResponse response = itineraryService.getItineraryItemById(eventId, itemId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error fetching itinerary item: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Reorder itinerary items")
    @PutMapping("/reorder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<MessageResponse> reorderItinerary(
            @PathVariable Long eventId,
            @RequestBody List<Long> itemIds) {
        try {
            log.info("Reordering itinerary for event ID: {}", eventId);
            MessageResponse response = itineraryService.reorderItinerary(eventId, itemIds);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error reordering itinerary: {}", e.getMessage());
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
