package com.merbsconnect.events.repository;

import com.merbsconnect.enums.MediaType;
import com.merbsconnect.events.model.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for event gallery items.
 */
@Repository
public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {

    /**
     * Find all gallery items for an event.
     */
    List<GalleryItem> findByEventId(Long eventId);

    /**
     * Find gallery items by event and media type.
     */
    List<GalleryItem> findByEventIdAndType(Long eventId, MediaType type);

    /**
     * Count gallery items for an event.
     */
    long countByEventId(Long eventId);

    /**
     * Delete all gallery items for an event.
     */
    void deleteByEventId(Long eventId);
}
