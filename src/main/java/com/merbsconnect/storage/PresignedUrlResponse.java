package com.merbsconnect.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for presigned URL generation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {

    /**
     * The object path/key in the bucket
     */
    private String path;

    /**
     * The presigned URL for accessing the object
     */
    private String presignedUrl;

    /**
     * Timestamp when the presigned URL expires
     */
    private Instant expiresAt;
}
