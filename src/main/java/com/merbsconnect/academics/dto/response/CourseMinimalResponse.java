package com.merbsconnect.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseMinimalResponse {
    private Long id;
    private String courseCode;
    private String courseName;
}