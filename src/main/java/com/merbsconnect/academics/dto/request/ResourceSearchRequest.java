package com.merbsconnect.academics.dto.request;

import com.merbsconnect.enums.QuizType;
import com.merbsconnect.enums.Semester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceSearchRequest {
    private String keyword;
    private String resourceType; // "QUIZ", "BOOK", etc.
    private Long collegeId;
    private Long facultyId;
    private Long departmentId;
    private Long programId;
    private Long courseId;
    private Semester semester;
    private String difficultyLevel; // For quizzes
    private QuizType quizType; // For quizzes
    private Integer yearFrom; // For quizzes
    private Integer yearTo; // For quizzes
    private String author; // For reference materials
    private String publisher; // For reference materials
    private String isbn; // For reference materials
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}