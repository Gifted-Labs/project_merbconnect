package com.merbsconnect.events.controller;

import com.merbsconnect.enums.MediaType;
import com.merbsconnect.events.dto.response.GalleryItemResponse;
import com.merbsconnect.events.dto.response.GalleryResponse;
import com.merbsconnect.events.service.GalleryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * REST controller for event gallery operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events/{eventId}/gallery")
@RequiredArgsConstructor
@Tag(name = "Event Gallery", description = "Event gallery media operations")
public class GalleryController {

    private final GalleryService galleryService;

    /**
     * Upload a media file to the event gallery.
     * Admin-only operation.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    @Operation(summary = "Upload media", description = "Upload an image or video to the event gallery (admin-only)")
    public ResponseEntity<GalleryItemResponse> uploadMedia(
            @PathVariable Long eventId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam("type") MediaType mediaType) throws IOException {

        log.info("Uploading {} to gallery for event {}: {}", mediaType, eventId, file.getOriginalFilename());
        GalleryItemResponse response = galleryService.uploadGalleryItem(eventId, file, caption, mediaType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get the complete gallery for an event.
     * Public endpoint.
     */
    @GetMapping
    @Operation(summary = "Get gallery", description = "Get all gallery items and Google Drive link for an event")
    public ResponseEntity<GalleryResponse> getGallery(@PathVariable Long eventId) {
        log.debug("Fetching gallery for event {}", eventId);
        GalleryResponse response = galleryService.getGallery(eventId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a gallery item.
     * Admin-only operation.
     */
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    @Operation(summary = "Delete gallery item", description = "Delete a media item from the gallery (admin-only)")
    public ResponseEntity<Void> deleteGalleryItem(
            @PathVariable Long eventId,
            @PathVariable Long itemId) {

        log.info("Deleting gallery item {} for event {}", itemId, eventId);
        galleryService.deleteGalleryItem(itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update the Google Drive folder link for an event.
     * Admin-only operation.
     */
    @PutMapping("/drive-link")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    @Operation(summary = "Update Drive link", description = "Set or update the Google Drive folder link (admin-only)")
    public ResponseEntity<Map<String, String>> updateGoogleDriveLink(
            @PathVariable Long eventId,
            @RequestBody Map<String, String> request) {

        String link = request.get("googleDriveFolderLink");
        log.info("Updating Google Drive link for event {}", eventId);
        galleryService.updateGoogleDriveLink(eventId, link);

        return ResponseEntity.ok(Map.of(
                "message", "Google Drive folder link updated successfully",
                "eventId", eventId.toString(),
                "googleDriveFolderLink", link != null ? link : ""));
    }
}
