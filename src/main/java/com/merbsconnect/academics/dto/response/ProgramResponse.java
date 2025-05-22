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
public class ProgramResponse {
    private Long id;
    private String programName;
    private String programCode;
    private Long departmentId;
    private String departmentName;
    private Long facultyId;
    private String facultyName;
    private Long collegeId;
    private String collegeName;
    private List<CourseMinimalResponse> courses;
}