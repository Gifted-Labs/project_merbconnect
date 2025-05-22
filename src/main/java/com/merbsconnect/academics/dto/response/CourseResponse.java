package com.merbsconnect.academics.dto.response;

import com.merbsconnect.enums.Semester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String courseCode;
    private String courseName;
    private String courseDescription;
    private Semester semester;
    private Long departmentId;
    private String departmentName;
    private Long facultyId;
    private String facultyName;
    private Long collegeId;
    private String collegeName;
    private List<ProgramMinimalResponse> programs;
}