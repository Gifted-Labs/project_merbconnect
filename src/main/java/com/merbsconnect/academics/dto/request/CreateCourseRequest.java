package com.merbsconnect.academics.dto.request;

import com.merbsconnect.enums.Semester;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    
    @NotBlank(message = "Course code is required")
    @Pattern(regexp = "^[A-Z]{2,4}\\d{3,4}$", message = "Course code must be in format: 2-4 uppercase letters followed by 3-4 digits")
    private String courseCode;
    
    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    private String courseName;
    
    @Size(max = 500, message = "Course description must not exceed 500 characters")
    private String courseDescription;
    
    @NotNull(message = "Semester is required")
    private Semester semester;
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    
    private Set<Long> programIds;
}