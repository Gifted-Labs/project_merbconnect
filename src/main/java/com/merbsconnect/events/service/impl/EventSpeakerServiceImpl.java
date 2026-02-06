package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.enums.MediaType;
import com.merbsconnect.events.dto.request.EventSpeakerRequest;
import com.merbsconnect.events.dto.response.EventSpeakerResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.EventSpeaker;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.repository.EventSpeakerRepository;
import com.merbsconnect.events.service.EventSpeakerService;
import com.merbsconnect.exception.BusinessException;
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
 * Service implementation for managing event speakers with S3 image upload
 * support.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventSpeakerServiceImpl implements EventSpeakerService {

    private final EventSpeakerRepository speakerRepository;
    private final EventRepository eventRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public EventSpeakerResponse addSpeaker(Long eventId, EventSpeakerRequest request) {
        Event event = getEventOrThrow(eventId);

        EventSpeaker speaker = EventSpeaker.builder()
                .event(event)
                .name(request.getName())
                .title(request.getTitle())
                .bio(request.getBio())
                .linkedinUrl(request.getLinkedinUrl())
                .twitterUrl(request.getTwitterUrl())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        EventSpeaker saved = speakerRepository.save(speaker);
        log.info("Added speaker '{}' to event ID {}", saved.getName(), eventId);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EventSpeakerResponse updateSpeaker(Long eventId, Long speakerId, EventSpeakerRequest request) {
        EventSpeaker speaker = getSpeakerOrThrow(eventId, speakerId);

        if (request.getName() != null) {
            speaker.setName(request.getName());
        }
        if (request.getTitle() != null) {
            speaker.setTitle(request.getTitle());
        }
        if (request.getBio() != null) {
            speaker.setBio(request.getBio());
        }
        if (request.getLinkedinUrl() != null) {
            speaker.setLinkedinUrl(request.getLinkedinUrl());
        }
        if (request.getTwitterUrl() != null) {
            speaker.setTwitterUrl(request.getTwitterUrl());
        }
        if (request.getDisplayOrder() != null) {
            speaker.setDisplayOrder(request.getDisplayOrder());
        }

        EventSpeaker updated = speakerRepository.save(speaker);
        log.info("Updated speaker ID {} for event ID {}", speakerId, eventId);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public MessageResponse deleteSpeaker(Long eventId, Long speakerId) {
        EventSpeaker speaker = getSpeakerOrThrow(eventId, speakerId);

        // Delete image from S3 if exists
        if (speaker.getImageUrl() != null && !speaker.getImageUrl().isEmpty()) {
            try {
                String key = storageService.extractKeyFromUrl(speaker.getImageUrl());
                storageService.deleteFile(key);
                log.info("Deleted speaker image from S3: {}", key);
            } catch (Exception e) {
                log.warn("Failed to delete speaker image from S3: {}", e.getMessage());
            }
        }

        speakerRepository.delete(speaker);
        log.info("Deleted speaker ID {} from event ID {}", speakerId, eventId);

        return MessageResponse.builder()
                .message("Speaker deleted successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "eventSpeakers", key = "#eventId")
    public List<EventSpeakerResponse> getSpeakers(Long eventId) {
        log.debug("Fetching speakers for event {}", eventId);
        // Verify event exists
        getEventOrThrow(eventId);

        return speakerRepository.findByEventIdOrderByDisplayOrderAsc(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventSpeakerResponse getSpeakerById(Long eventId, Long speakerId) {
        return mapToResponse(getSpeakerOrThrow(eventId, speakerId));
    }

    @Override
    @Transactional
    public EventSpeakerResponse uploadSpeakerImage(Long eventId, Long speakerId, MultipartFile image)
            throws IOException {
        EventSpeaker speaker = getSpeakerOrThrow(eventId, speakerId);

        // Delete old image if exists
        if (speaker.getImageUrl() != null && !speaker.getImageUrl().isEmpty()) {
            try {
                String oldKey = storageService.extractKeyFromUrl(speaker.getImageUrl());
                storageService.deleteFile(oldKey);
                log.info("Deleted old speaker image: {}", oldKey);
            } catch (Exception e) {
                log.warn("Failed to delete old speaker image: {}", e.getMessage());
            }
        }

        // Upload new image to S3
        String imageUrl = storageService.uploadGalleryItem(eventId, image, MediaType.IMAGE);
        speaker.setImageUrl(imageUrl);

        EventSpeaker updated = speakerRepository.save(speaker);
        log.info("Uploaded new image for speaker ID {}: {}", speakerId, imageUrl);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public MessageResponse deleteSpeakerImage(Long eventId, Long speakerId) {
        EventSpeaker speaker = getSpeakerOrThrow(eventId, speakerId);

        if (speaker.getImageUrl() == null || speaker.getImageUrl().isEmpty()) {
            throw new BusinessException("Speaker does not have an image");
        }

        try {
            String key = storageService.extractKeyFromUrl(speaker.getImageUrl());
            storageService.deleteFile(key);
            log.info("Deleted speaker image from S3: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete speaker image from S3: {}", e.getMessage());
            throw new BusinessException("Failed to delete speaker image");
        }

        speaker.setImageUrl(null);
        speakerRepository.save(speaker);

        return MessageResponse.builder()
                .message("Speaker image deleted successfully")
                .build();
    }

    // ===== Helper Methods =====

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found with ID: " + eventId));
    }

    private EventSpeaker getSpeakerOrThrow(Long eventId, Long speakerId) {
        EventSpeaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new BusinessException("Speaker not found with ID: " + speakerId));

        if (!speaker.getEvent().getId().equals(eventId)) {
            throw new BusinessException("Speaker does not belong to event ID: " + eventId);
        }

        return speaker;
    }

    private EventSpeakerResponse mapToResponse(EventSpeaker speaker) {
        String presignedUrl = null;
        if (speaker.getImageUrl() != null && !speaker.getImageUrl().isEmpty()) {
            presignedUrl = storageService.generatePresignedUrl(speaker.getImageUrl());
        }

        return EventSpeakerResponse.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .title(speaker.getTitle())
                .bio(speaker.getBio())
                .imageUrl(presignedUrl)
                .linkedinUrl(speaker.getLinkedinUrl())
                .twitterUrl(speaker.getTwitterUrl())
                .displayOrder(speaker.getDisplayOrder())
                .createdAt(speaker.getCreatedAt())
                .updatedAt(speaker.getUpdatedAt())
                .build();
    }
}
