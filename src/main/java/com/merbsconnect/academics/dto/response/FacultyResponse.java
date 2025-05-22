package com.merbsconnect.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyResponse {
    private Long id;
    private String facultyName;
    private Long collegeId;
    private String collegeName;
}