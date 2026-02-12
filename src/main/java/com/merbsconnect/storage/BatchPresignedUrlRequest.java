package com.merbsconnect.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Request DTO for batch presigned URL generation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchPresignedUrlRequest {

    /**
     * List of object paths/keys to generate presigned URLs for
     */
    @NotEmpty(message = "Paths list cannot be empty")
    private List<String> paths;
}
