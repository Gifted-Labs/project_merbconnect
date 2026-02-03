package com.merbsconnect.events.model;

import com.merbsconnect.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a gallery item (image or video) for an event.
 * Media is stored in Railway Storage Bucket (S3-compatible).
 */
@Entity
@Table(name = "event_gallery_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "event")
@EqualsAndHashCode(exclude = "event")
public class GalleryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String mediaUrl; // URL or key in storage bucket

    private String caption;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type; // IMAGE or VIDEO

    @Column(nullable = false)
    private String fileName; // Original file name

    private Long fileSize; // File size in bytes

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
