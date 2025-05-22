package com.merbsconnect.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String questionText;
    private List<String> possibleAnswers;
    private String correctAnswer;
    private List<String> explanationSteps;
    private ResourceMinimalResponse referencedResource;
}