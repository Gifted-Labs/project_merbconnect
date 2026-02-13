package com.merbsconnect.startright.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequestDto {

    @NotBlank(message = "Question content is required")
    @Size(min = 5, max = 1000, message = "Question must be between 5 and 1000 characters")
    private String content;

    private String studentName;

    private String program;

    private String academicLevel;

    @Builder.Default
    private Boolean isAnonymous = false;
}
