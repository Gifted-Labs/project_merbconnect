package com.merbsconnect.academics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSearchRequest {
    private String keyword;
    private String difficultyLevel;
    private Long referencedResourceId;
    private List<String> tags;
    private Boolean unusedOnly;
    private Long courseId;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}