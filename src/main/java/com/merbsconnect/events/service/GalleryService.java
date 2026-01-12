package com.merbsconnect.events.service;

import com.merbsconnect.enums.MediaType;
import com.merbsconnect.events.dto.response.GalleryItemResponse;
import com.merbsconnect.events.dto.response.GalleryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface for event gallery operations.
 */
public interface GalleryService {

    /**
     * Uploads a media file to the event gallery.
     * Admin-only operation.
     *
     * @param eventId   The event ID
     * @param file      The file to upload
     * @param caption   Optional caption for the media
     * @param mediaType The type of media (IMAGE or VIDEO)
     * @return The created gallery item
     */
    GalleryItemResponse uploadGalleryItem(Long eventId, MultipartFile file, String caption, MediaType mediaType)
            throws IOException;

    /**
     * Gets the complete gallery for an event.
     *
     * @param eventId The event ID
     * @return The gallery with all items and Google Drive link
     */
    GalleryResponse getGallery(Long eventId);

    /**
     * Deletes a gallery item.
     * Admin-only operation.
     *
     * @param itemId The gallery item ID
     */
    void deleteGalleryItem(Long itemId);

    /**
     * Sets or updates the Google Drive folder link for an event.
     * Admin-only operation.
     *
     * @param eventId The event ID
     * @param link    The Google Drive folder link
     */
    void updateGoogleDriveLink(Long eventId, String link);
}
