package com.merbsconnect.academics.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkResourceRequest<T> {
    
    @NotEmpty(message = "At least one resource is required")
    @Valid
    private List<T> resources;
}