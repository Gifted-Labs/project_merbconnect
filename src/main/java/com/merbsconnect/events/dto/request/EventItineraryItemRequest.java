package com.merbsconnect.events.dto.request;

import com.merbsconnect.events.model.ItineraryItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for creating/updating an event itinerary item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventItineraryItemRequest {

    /**
     * Title of the activity (e.g., "Opening Prayer", "Keynote Speech")
     */
    private String title;

    /**
     * Optional description of the activity
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
     * Optional speaker or facilitator for this activity
     */
    private String speakerName;

    /**
     * Optional venue/room for this activity
     */
    private String venue;

    /**
     * Order/sequence of the activity in the program
     */
    private Integer displayOrder;

    /**
     * Type of activity
     */
    private ItineraryItemType itemType;

    /**
     * Duration in minutes (optional - can be calculated from start/end times)
     */
    private Integer durationMinutes;
}
