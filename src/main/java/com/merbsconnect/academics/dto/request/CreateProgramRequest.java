package com.merbsconnect.academics.dto.request;

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
public class CreateProgramRequest {
    
    @NotBlank(message = "Program name is required")
    @Size(min = 2, max = 100, message = "Program name must be between 2 and 100 characters")
    private String programName;
    
    @NotBlank(message = "Program code is required")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "Program code must be 2-10 uppercase letters or numbers")
    private String programCode;
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    
    private Set<Long> courseIds;
}