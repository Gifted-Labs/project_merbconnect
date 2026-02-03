package com.merbsconnect.events.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a speaker for an event.
 * Speakers are now stored as separate entities to support image upload to S3.
 */
@Entity
@Table(name = "event_speakers_v2")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "event")
@EqualsAndHashCode(exclude = "event")
public class EventSpeaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotNull
    @Column(nullable = false)
    private String name;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * S3 URL for the speaker's image.
     * This is populated after uploading an image via the speaker image upload
     * endpoint.
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
    @Builder.Default
    private Integer displayOrder = 0;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
