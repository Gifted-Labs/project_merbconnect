package com.merbsconnect.academics.dto.response;

import com.merbsconnect.enums.QuizType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private int numberOfQuestions;
    private String difficultyLevel;
    private int yearGiven;
    private QuizType quizType;
    private List<QuestionResponse> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}