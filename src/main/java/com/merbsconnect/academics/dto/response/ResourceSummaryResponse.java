package com.merbsconnect.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private String resourceType;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for specific resource types
    private String difficultyLevel; // For quizzes
    private Integer yearGiven; // For quizzes
    private String quizType; // For quizzes
    
    private String author; // For reference materials
    private String format;
    private String publisher; // For reference materials
    private String coverImageUrl; // For reference materials
}