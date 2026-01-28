package com.merbsconnect.events.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating an event speaker.
 * Note: Image upload is handled separately via multipart endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSpeakerRequest {

    private String name;

    private String title;

    private String bio;

    /**
     * Optional LinkedIn profile URL
     */
    private String linkedinUrl;

    /**
     * Optional Twitter/X profile URL
     */
    private String twitterUrl;

    /**
     * Display order for sorting speakers
     */
    private Integer displayOrder;
}
