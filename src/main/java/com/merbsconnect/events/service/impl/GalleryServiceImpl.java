package com.merbsconnect.events.service.impl;

import com.merbsconnect.enums.MediaType;
import com.merbsconnect.events.dto.response.GalleryItemResponse;
import com.merbsconnect.events.dto.response.GalleryResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.GalleryItem;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.repository.GalleryItemRepository;
import com.merbsconnect.events.service.GalleryService;
import com.merbsconnect.exception.ResourceNotFoundException;
import com.merbsconnect.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of GalleryService for managing event galleries with Railway
 * bucket storage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

        private final GalleryItemRepository galleryItemRepository;
        private final EventRepository eventRepository;
        private final StorageService storageService;

        @Override
        @Transactional
        public GalleryItemResponse uploadGalleryItem(Long eventId, MultipartFile file, String caption,
                        MediaType mediaType)
                        throws IOException {
                log.info("Uploading {} for event {}: {}", mediaType, eventId, file.getOriginalFilename());

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Event not found with id: " + eventId));

                // Upload to Railway bucket
                String mediaUrl = storageService.uploadGalleryItem(eventId, file, mediaType);

                GalleryItem item = GalleryItem.builder()
                                .event(event)
                                .mediaUrl(mediaUrl)
                                .caption(caption)
                                .type(mediaType)
                                .fileName(file.getOriginalFilename())
                                .fileSize(file.getSize())
                                .build();

                GalleryItem savedItem = galleryItemRepository.save(item);
                log.info("Gallery item created with id: {}", savedItem.getId());

                return mapToResponse(savedItem);
        }

        @Override
        @Transactional(readOnly = true)
        public GalleryResponse getGallery(Long eventId) {
                log.debug("Fetching gallery for event {}", eventId);

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Event not found with id: " + eventId));

                List<GalleryItem> items = galleryItemRepository.findByEventId(eventId);
                long totalItems = galleryItemRepository.countByEventId(eventId);

                List<GalleryItemResponse> itemResponses = items.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());

                return GalleryResponse.builder()
                                .eventId(eventId)
                                .eventTitle(event.getTitle())
                                .googleDriveFolderLink(event.getGoogleDriveFolderLink())
                                .items(itemResponses)
                                .totalItems(totalItems)
                                .build();
        }

        @Override
        @Transactional
        public void deleteGalleryItem(Long itemId) {
                log.info("Deleting gallery item {}", itemId);

                GalleryItem item = galleryItemRepository.findById(itemId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Gallery item not found with id: " + itemId));

                // Delete from storage bucket
                try {
                        String key = storageService.extractKeyFromUrl(item.getMediaUrl());
                        storageService.deleteFile(key);
                } catch (Exception e) {
                        log.warn("Failed to delete file from storage: {}", item.getMediaUrl(), e);
                        // Continue with database deletion even if storage deletion fails
                }

                galleryItemRepository.delete(item);
                log.info("Gallery item {} deleted successfully", itemId);
        }

        @Override
        @Transactional
        public void updateGoogleDriveLink(Long eventId, String link) {
                log.info("Updating Google Drive link for event {}", eventId);

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Event not found with id: " + eventId));

                event.setGoogleDriveFolderLink(link);
                eventRepository.save(event);

                log.info("Google Drive link updated for event {}", eventId);
        }

        /**
         * Maps a GalleryItem entity to a GalleryItemResponse DTO.
         * Generates a presigned URL for private S3 storage access.
         */
        private GalleryItemResponse mapToResponse(GalleryItem item) {
                // Generate presigned URL for private S3 bucket access
                String presignedUrl = storageService.generatePresignedUrl(item.getMediaUrl());

                return GalleryItemResponse.builder()
                                .id(item.getId())
                                .eventId(item.getEvent().getId())
                                .mediaUrl(presignedUrl)
                                .caption(item.getCaption())
                                .type(item.getType())
                                .fileName(item.getFileName())
                                .fileSize(item.getFileSize())
                                .createdAt(item.getCreatedAt())
                                .build();
        }

}
