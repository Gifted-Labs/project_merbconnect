package com.merbsconnect.academics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class CreateStandaloneQuestionRequest {
    
    @NotBlank(message = "Question text is required")
    @Size(min = 5, max = 1000, message = "Question text must be between 5 and 1000 characters")
    private String questionText;
    
    @NotEmpty(message = "At least two possible answers are required")
    @Size(min = 2, message = "At least two possible answers are required")
    private List<String> possibleAnswers;
    
    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;
    
    private List<String> explanationSteps;
    
    private Long referencedResourceId;
    
    private String difficultyLevel;
    
    private List<String> tags;
}