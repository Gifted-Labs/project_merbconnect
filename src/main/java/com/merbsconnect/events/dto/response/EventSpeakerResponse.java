package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning speaker information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSpeakerResponse implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String title;

    private String bio;

    /**
     * S3 URL for the speaker's image.
     */
    private String imageUrl;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
