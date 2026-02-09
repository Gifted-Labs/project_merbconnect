package com.merbsconnect.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for system configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigResponse {

    private String configKey;
    private String configValue;
    private String description;
    private String category;
    private String type;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
