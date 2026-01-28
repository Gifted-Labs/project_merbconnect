package com.merbsconnect.events.service;

import com.merbsconnect.events.dto.request.CreateReviewRequest;
import com.merbsconnect.events.dto.response.ReviewPageResponse;
import com.merbsconnect.events.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for event review operations.
 */
public interface ReviewService {

    /**
     * Creates a new review for an event.
     * Only authenticated users can create reviews.
     * Users cannot submit duplicate reviews for the same event.
     *
     * @param eventId The event ID
     * @param request The review request
     * @param userId  The authenticated user ID
     * @return The created review
     */
    ReviewResponse createReview(Long eventId, CreateReviewRequest request, Long userId);

    /**
     * Gets paginated reviews for an event with statistics.
     *
     * @param eventId  The event ID
     * @param pageable Pagination parameters
     * @return Paginated reviews with average rating and distribution
     */
    ReviewPageResponse getReviewsForEvent(Long eventId, Pageable pageable);

    /**
     * Updates an existing review.
     * Only the review owner can update their review.
     *
     * @param reviewId The review ID
     * @param request  The update request
     * @param userId   The authenticated user ID
     * @return The updated review
     */
    ReviewResponse updateReview(Long reviewId, CreateReviewRequest request, Long userId);

    /**
     * Deletes a review.
     * The review owner or admin can delete a review.
     *
     * @param reviewId The review ID
     * @param userId   The authenticated user ID (null if admin)
     * @param isAdmin  Whether the request is from an admin
     */
    void deleteReview(Long reviewId, Long userId, boolean isAdmin);

    /**
     * Gets the average rating for an event.
     *
     * @param eventId The event ID
     * @return The average rating (1-5) or null if no reviews
     */
    Double getAverageRating(Long eventId);
}
