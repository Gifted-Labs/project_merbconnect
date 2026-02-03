package com.merbsconnect.events.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = { "registrations", "reviews", "articles", "galleryItems", "registrationsV2", "speakersV2",
        "itinerary", "testimonials", "gallery" })
@EqualsAndHashCode(exclude = { "registrations", "reviews", "articles", "galleryItems", "registrationsV2", "speakersV2",
        "itinerary", "testimonials", "gallery" })
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private LocalDate date;

    private LocalTime time;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
    private Gallery gallery;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_sponsors", joinColumns = @JoinColumn(name = "event_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Sponsors> sponsors = new HashSet<>();

    @Column(length = 2048)
    private String imageUrl;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_speakers", joinColumns = @JoinColumn(name = "event_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Speaker> speakers = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Testimonials> testimonials;

    @Column(length = 2048)
    private String videoUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_contacts", joinColumns = @JoinColumn(name = "event_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Contact> contacts = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_registrations", joinColumns = @JoinColumn(name = "event_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Registration> registrations = new HashSet<>();

    // ===== New Fields for Backend Enhancements =====

    /**
     * Optional Google Drive folder link for external gallery hosting.
     */
    @Column(length = 2048)
    private String googleDriveFolderLink;

    /**
     * Event reviews from authenticated users.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Review> reviews = new java.util.LinkedHashSet<>();

    /**
     * Speaker articles/transcripts for this event.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Article> articles = new java.util.LinkedHashSet<>();

    /**
     * Gallery items (images/videos) stored in Railway bucket.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<GalleryItem> galleryItems = new java.util.LinkedHashSet<>();

    /**
     * Enhanced registrations with QR codes and check-in support.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<EventRegistration> registrationsV2 = new java.util.LinkedHashSet<>();

    // ===== New Fields for V2 Enhancements =====

    /**
     * Theme of the event (e.g., "Innovation & Technology", "Faith & Leadership")
     */
    @Column(length = 1000)
    private String theme;

    /**
     * Enhanced speakers stored as entities (supports S3 image upload).
     * This replaces the embedded speakers for new events.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<EventSpeaker> speakersV2 = new java.util.LinkedHashSet<>();

    /**
     * Event itinerary/program lineup - ordered list of activities.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<EventItineraryItem> itinerary = new java.util.LinkedHashSet<>();

    @PrePersist
    public void init() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = (LocalDateTime.now());

    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
