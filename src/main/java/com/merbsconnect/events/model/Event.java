package com.merbsconnect.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String location;

    private LocalDate date;

    private LocalTime time;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
    private Gallery gallery;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_sponsors", joinColumns = @JoinColumn(name = "event_id"))
    private Set<Sponsors> sponsors = new HashSet<>();

    private String imageUrl;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_speakers", joinColumns = @JoinColumn(name = "event_id"))
    private Set<Speaker> speakers = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Testimonials> testimonials;

    private String videoUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_contacts", joinColumns = @JoinColumn(name = "event_id"))
    private Set<Contact> contacts = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_registrations", joinColumns = @JoinColumn(name = "event_id"))
    private Set<Registration> registrations = new HashSet<>();

    // ===== New Fields for Backend Enhancements =====

    /**
     * Optional Google Drive folder link for external gallery hosting.
     */
    private String googleDriveFolderLink;

    /**
     * Event reviews from authenticated users.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    /**
     * Speaker articles/transcripts for this event.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Article> articles = new ArrayList<>();

    /**
     * Gallery items (images/videos) stored in Railway bucket.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<GalleryItem> galleryItems = new ArrayList<>();

    /**
     * Enhanced registrations with QR codes and check-in support.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EventRegistration> registrationsV2 = new ArrayList<>();

    @PrePersist
    public void init() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = (LocalDateTime.now());

    }
}
