package com.merbsconnect.academics.dto.request;

import com.merbsconnect.enums.QuizType;
import com.merbsconnect.enums.Semester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceFilterRequest {
    private String resourceType;
    private Long collegeId;
    private Long facultyId;
    private Long departmentId;
    private Long programId;
    private Long courseId;
    private Semester semester;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;
    
    // Quiz specific filters
    private String difficultyLevel;
    private QuizType quizType;
    private Integer yearFrom;
    private Integer yearTo;
    
    // Reference material specific filters
    private String author;
    private String publisher;
    private String isbn;
    private String format;
    
    // Pagination and sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}