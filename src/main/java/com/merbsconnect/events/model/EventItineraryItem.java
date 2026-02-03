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

import java.time.LocalTime;

/**
 * Entity representing an itinerary item (program lineup item) for an event.
 * This allows defining the flow/schedule of activities during an event.
 */
@Entity
@Table(name = "event_itinerary_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "event")
@EqualsAndHashCode(exclude = "event")
public class EventItineraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * Title of the activity (e.g., "Opening Prayer", "Keynote Speech", "Networking
     * Session")
     */
    @NotNull
    @Column(nullable = false)
    private String title;

    /**
     * Optional description of the activity
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Start time of the activity
     */
    private LocalTime startTime;

    /**
     * End time of the activity
     */
    private LocalTime endTime;

    /**
     * Optional speaker or facilitator for this activity
     */
    private String speakerName;

    /**
     * Optional venue/room for this activity (if different from main event venue)
     */
    private String venue;

    /**
     * Order/sequence of the activity in the program
     */
    @Builder.Default
    private Integer displayOrder = 0;

    /**
     * Type of activity (e.g., CEREMONY, SESSION, BREAK, NETWORKING, WORSHIP, OTHER)
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ItineraryItemType itemType = ItineraryItemType.SESSION;

    /**
     * Duration in minutes (calculated or manually set)
     */
    private Integer durationMinutes;
}
