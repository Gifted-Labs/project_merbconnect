package com.merbsconnect.academics.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionToQuizRequest {
    
    @NotEmpty(message = "At least one question is required")
    @Valid
    private List<CreateQuestionRequest> questions;
}