package com.merbsconnect.startright.dto.response;

import com.merbsconnect.startright.enums.QuestionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseDto {

    private Long id;
    private String content;
    private String studentName;
    private String program;
    private String academicLevel;
    private Boolean isAnonymous;
    private QuestionStatus status;
    private LocalDateTime createdAt;
}
