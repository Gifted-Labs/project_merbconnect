package com.merbsconnect.events.repository;

import com.merbsconnect.events.model.EventItineraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for EventItineraryItem entity operations.
 */
@Repository
public interface EventItineraryItemRepository extends JpaRepository<EventItineraryItem, Long> {

    /**
     * Find all itinerary items for a specific event, ordered by display order then
     * start time.
     */
    List<EventItineraryItem> findByEventIdOrderByDisplayOrderAscStartTimeAsc(Long eventId);

    /**
     * Count itinerary items for a specific event.
     */
    long countByEventId(Long eventId);

    /**
     * Delete all itinerary items for a specific event.
     */
    void deleteByEventId(Long eventId);
}
