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
public class UpdateCollegeRequest {
    
    @NotBlank(message = "College name is required")
    @Size(min = 2, max = 100, message = "College name must be between 2 and 100 characters")
    private String collegeName;
}