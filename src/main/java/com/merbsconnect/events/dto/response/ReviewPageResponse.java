package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for paginated reviews with aggregate statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPageResponse {

    private List<ReviewResponse> reviews;
    private Double averageRating;
    private Long totalReviews;
    private Map<Integer, Long> ratingDistribution; // Star rating -> count
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
