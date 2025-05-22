package com.merbsconnect.academics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFacultyRequest {
    
    @NotBlank(message = "Faculty name is required")
    @Size(min = 2, max = 100, message = "Faculty name must be between 2 and 100 characters")
    private String facultyName;
    
    private Long collegeId;
}