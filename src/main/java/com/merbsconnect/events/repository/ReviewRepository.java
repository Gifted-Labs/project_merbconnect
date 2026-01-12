package com.merbsconnect.events.repository;

import com.merbsconnect.events.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for event reviews.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find all reviews for an event with pagination.
     */
    Page<Review> findByEventId(Long eventId, Pageable pageable);

    /**
     * Find a specific review by event and user.
     */
    Optional<Review> findByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Check if a user has already reviewed an event.
     */
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Calculate average rating for an event.
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.event.id = :eventId")
    Double getAverageRating(@Param("eventId") Long eventId);

    /**
     * Get rating distribution for an event (count per star rating).
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.event.id = :eventId GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistribution(@Param("eventId") Long eventId);

    /**
     * Count total reviews for an event.
     */
    long countByEventId(Long eventId);
}
