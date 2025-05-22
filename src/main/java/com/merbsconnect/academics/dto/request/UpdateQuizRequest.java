package com.merbsconnect.academics.dto.request;

import com.merbsconnect.enums.QuizType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuizRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Long courseId;
    
    @Min(value = 1, message = "Number of questions must be at least 1")
    @Max(value = 100, message = "Number of questions cannot exceed 100")
    private int numberOfQuestions;
    
    @NotBlank(message = "Difficulty level is required")
    @Size(min = 2, max = 20, message = "Difficulty level must be between 2 and 20 characters")
    private String difficultyLevel;
    
    @Min(value = 1900, message = "Year given must be at least 1900")
    @Max(value = 2100, message = "Year given cannot exceed 2100")
    private int yearGiven;
    
    private QuizType quizType;
    
    @Valid
    private List<UpdateQuestionRequest> questions;
}