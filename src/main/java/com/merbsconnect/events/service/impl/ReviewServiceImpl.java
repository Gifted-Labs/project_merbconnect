package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.enums.AcademicLevel;
import com.merbsconnect.events.dto.request.CreateReviewRequest;
import com.merbsconnect.events.dto.response.ReviewPageResponse;
import com.merbsconnect.events.dto.response.ReviewResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.Review;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.repository.ReviewRepository;
import com.merbsconnect.events.service.ReviewService;
import com.merbsconnect.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ReviewService for managing event reviews.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(Long eventId, CreateReviewRequest request, Long userId) {
        log.info("Creating review for event {} by user/guest {}", eventId, userId);

        // Check if event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        User user = null;
        if (userId != null) {
            // Check if user exists
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            // Check for duplicate review for registered users
            if (reviewRepository.existsByEventIdAndUserId(eventId, userId)) {
                throw new IllegalStateException(
                        "You have already reviewed this event. You can only update your existing review.");
            }
        }

        Review review = Review.builder()
                .event(event)
                .user(user)
                .guestName(request.getGuestName())
                .guestAcademicLevel(request.getGuestAcademicLevel())
                .guestProgram(request.getGuestProgram())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review created with id: {}", savedReview.getId());

        return mapToResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewPageResponse getReviewsForEvent(Long eventId, Pageable pageable) {
        log.debug("Fetching reviews for event {} with pageable: {}", eventId, pageable);

        // Verify event exists
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }

        Page<Review> reviewPage = reviewRepository.findByEventId(eventId, pageable);
        Double averageRating = reviewRepository.getAverageRating(eventId);
        long totalReviews = reviewRepository.countByEventId(eventId);

        // Get rating distribution
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        List<Object[]> distribution = reviewRepository.getRatingDistribution(eventId);
        for (Object[] row : distribution) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            ratingDistribution.put(rating, count);
        }

        // Fill in zeros for missing ratings
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.putIfAbsent(i, 0L);
        }

        List<ReviewResponse> reviews = reviewPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ReviewPageResponse.builder()
                .reviews(reviews)
                .averageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : null)
                .totalReviews(totalReviews)
                .ratingDistribution(ratingDistribution)
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, CreateReviewRequest request, Long userId) {
        log.info("Updating review {} by user {}", reviewId, userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Verify ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        log.info("Review {} updated successfully", reviewId);

        return mapToResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId, boolean isAdmin) {
        log.info("Deleting review {} by user {} (isAdmin: {})", reviewId, userId, isAdmin);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Verify ownership or admin
        if (!isAdmin && !review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
        log.info("Review {} deleted successfully", reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long eventId) {
        return reviewRepository.getAverageRating(eventId);
    }

    /**
     * Maps a Review entity to a ReviewResponse DTO.
     */
    private ReviewResponse mapToResponse(Review review) {
        boolean isGuest = review.getUser() == null;
        String userName = isGuest ? review.getGuestName()
                : (review.getUser().getFirstName() + " " + review.getUser().getLastName());

        return ReviewResponse.builder()
                .id(review.getId())
                .eventId(review.getEvent().getId())
                .userId(isGuest ? null : review.getUser().getId())
                .userName(userName)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .guestName(review.getGuestName())
                .guestAcademicLevel(review.getGuestAcademicLevel())
                .guestProgram(review.getGuestProgram())
                .isGuest(isGuest)
                .build();
    }
}
