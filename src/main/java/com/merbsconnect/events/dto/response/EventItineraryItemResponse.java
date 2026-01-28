package com.merbsconnect.events.dto.response;

import com.merbsconnect.events.model.ItineraryItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for returning itinerary item information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventItineraryItemResponse {

    private Long id;

    /**
     * Title of the activity
     */
    private String title;

    /**
     * Description of the activity
     */
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
     * Speaker or facilitator name
     */
    private String speakerName;

    /**
     * Venue/room for this activity
     */
    private String venue;

    /**
     * Order/sequence of the activity
     */
    private Integer displayOrder;

    /**
     * Type of activity
     */
    private ItineraryItemType itemType;

    /**
     * Display name of the item type
     */
    private String itemTypeDisplayName;

    /**
     * Duration in minutes
     */
    private Integer durationMinutes;
}
