package com.merbsconnect.events.repository;

import com.merbsconnect.events.model.EventSpeaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for EventSpeaker entity operations.
 */
@Repository
public interface EventSpeakerRepository extends JpaRepository<EventSpeaker, Long> {

    /**
     * Find all speakers for a specific event, ordered by display order.
     */
    List<EventSpeaker> findByEventIdOrderByDisplayOrderAsc(Long eventId);

    /**
     * Find a speaker by event ID and speaker name.
     */
    @Query("SELECT s FROM EventSpeaker s WHERE s.event.id = :eventId AND LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<EventSpeaker> findByEventIdAndNameContaining(Long eventId, String name);

    /**
     * Count speakers for a specific event.
     */
    long countByEventId(Long eventId);

    /**
     * Delete all speakers for a specific event.
     */
    void deleteByEventId(Long eventId);
}
