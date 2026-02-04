package com.merbsconnect.events.controller;

import com.merbsconnect.authentication.security.CustomUserDetails;
import com.merbsconnect.events.dto.request.CreateReviewRequest;
import com.merbsconnect.events.dto.response.ReviewPageResponse;
import com.merbsconnect.events.dto.response.ReviewResponse;
import com.merbsconnect.events.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for event review operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events/{eventId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Event Reviews", description = "Event review and rating operations")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Submit a new review for an event.
     * Open to authenticated users and guests. Users can only submit one review per
     * event.
     */
    @PostMapping
    @Operation(summary = "Submit a review", description = "Submit a new review for an event (public/authenticated)")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal(expression = "#this") CustomUserDetails userDetails) {

        Long userId = userDetails != null ? userDetails.getId() : null;
        log.info("Creating review for event {} (userId: {})", eventId, userId);
        ReviewResponse response = reviewService.createReview(eventId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all reviews for an event with pagination and statistics.
     * Public endpoint - anyone can view reviews.
     */
    @GetMapping
    @Operation(summary = "Get reviews", description = "Get paginated reviews for an event with average rating")
    public ResponseEntity<ReviewPageResponse> getReviews(
            @PathVariable Long eventId,
            @PageableDefault(size = 10) Pageable pageable) {

        log.debug("Fetching reviews for event {}", eventId);
        ReviewPageResponse response = reviewService.getReviewsForEvent(eventId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing review.
     * Only the review owner can update their review.
     */
    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update a review", description = "Update your own review")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long eventId,
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("Updating review {} for event {} by user {}", reviewId, eventId, userDetails.getId());
        ReviewResponse response = reviewService.updateReview(reviewId, request, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a review.
     * The review owner or admin can delete a review.
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete a review", description = "Delete your own review or moderate as admin")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long eventId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("Deleting review {} for event {} by user {}", reviewId, eventId, userDetails.getId());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN"));

        reviewService.deleteReview(reviewId, userDetails.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}
