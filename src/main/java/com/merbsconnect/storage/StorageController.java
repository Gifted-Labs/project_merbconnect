package com.merbsconnect.storage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for storage operations, particularly presigned URL generation
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Tag(name = "Storage", description = "Storage and presigned URL operations")
public class StorageController {

    private final StorageService storageService;

    /**
     * Expiration duration for presigned URLs (1 hour in seconds)
     */
    private static final long EXPIRATION_SECONDS = 3600;

    /**
     * Generate a presigned URL for a single object path
     * 
     * @param path The object path/key in the bucket
     * @return Presigned URL response with URL and expiration time
     */
    @GetMapping("/presigned-url")
    @Operation(summary = "Generate presigned URL", description = "Generate a temporary presigned URL for accessing an object in R2 storage")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(@RequestParam String path) {
        log.debug("Generating presigned URL for path: {}", path);

        if (path == null || path.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String presignedUrl = storageService.generatePresignedUrl(path);

        if (presignedUrl == null) {
            log.error("Failed to generate presigned URL for path: {}", path);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        PresignedUrlResponse response = PresignedUrlResponse.builder()
                .path(path)
                .presignedUrl(presignedUrl)
                .expiresAt(Instant.now().plusSeconds(EXPIRATION_SECONDS))
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Generate presigned URLs for multiple object paths (batch operation)
     * 
     * @param request Batch request containing list of paths
     * @return List of presigned URL responses
     */
    @PostMapping("/presigned-urls")
    @Operation(summary = "Generate batch presigned URLs", description = "Generate temporary presigned URLs for multiple objects in R2 storage")
    public ResponseEntity<List<PresignedUrlResponse>> generateBatchPresignedUrls(
            @Valid @RequestBody BatchPresignedUrlRequest request) {

        log.debug("Generating presigned URLs for {} paths", request.getPaths().size());

        List<PresignedUrlResponse> responses = request.getPaths().stream()
                .map(path -> {
                    String presignedUrl = storageService.generatePresignedUrl(path);
                    return PresignedUrlResponse.builder()
                            .path(path)
                            .presignedUrl(presignedUrl)
                            .expiresAt(Instant.now().plusSeconds(EXPIRATION_SECONDS))
                            .build();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
