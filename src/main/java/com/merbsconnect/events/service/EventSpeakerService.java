package com.merbsconnect.events.service;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.EventSpeakerRequest;
import com.merbsconnect.events.dto.response.EventSpeakerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for managing event speakers with S3 image upload support.
 */
public interface EventSpeakerService {

    /**
     * Add a new speaker to an event.
     */
    EventSpeakerResponse addSpeaker(Long eventId, EventSpeakerRequest request);

    /**
     * Update an existing speaker.
     */
    EventSpeakerResponse updateSpeaker(Long eventId, Long speakerId, EventSpeakerRequest request);

    /**
     * Delete a speaker from an event.
     */
    MessageResponse deleteSpeaker(Long eventId, Long speakerId);

    /**
     * Get all speakers for an event.
     */
    List<EventSpeakerResponse> getSpeakers(Long eventId);

    /**
     * Get a specific speaker by ID.
     */
    EventSpeakerResponse getSpeakerById(Long eventId, Long speakerId);

    /**
     * Upload an image for a speaker to S3.
     */
    EventSpeakerResponse uploadSpeakerImage(Long eventId, Long speakerId, MultipartFile image) throws IOException;

    /**
     * Delete a speaker's image from S3.
     */
    MessageResponse deleteSpeakerImage(Long eventId, Long speakerId);
}
