package com.merbsconnect.events.service;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.EventItineraryItemRequest;
import com.merbsconnect.events.dto.response.EventItineraryItemResponse;

import java.util.List;

/**
 * Service interface for managing event itinerary/program lineup.
 */
public interface EventItineraryService {

    /**
     * Add a new itinerary item to an event.
     */
    EventItineraryItemResponse addItineraryItem(Long eventId, EventItineraryItemRequest request);

    /**
     * Update an existing itinerary item.
     */
    EventItineraryItemResponse updateItineraryItem(Long eventId, Long itemId, EventItineraryItemRequest request);

    /**
     * Delete an itinerary item from an event.
     */
    MessageResponse deleteItineraryItem(Long eventId, Long itemId);

    /**
     * Get all itinerary items for an event (ordered by display order/time).
     */
    List<EventItineraryItemResponse> getItinerary(Long eventId);

    /**
     * Get a specific itinerary item by ID.
     */
    EventItineraryItemResponse getItineraryItemById(Long eventId, Long itemId);

    /**
     * Reorder itinerary items.
     * 
     * @param eventId The event ID
     * @param itemIds List of item IDs in the desired order
     */
    MessageResponse reorderItinerary(Long eventId, List<Long> itemIds);

    /**
     * Bulk add itinerary items.
     */
    List<EventItineraryItemResponse> bulkAddItineraryItems(Long eventId, List<EventItineraryItemRequest> items);
}
