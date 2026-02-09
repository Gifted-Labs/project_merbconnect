package com.merbsconnect.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating system configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigRequest {

    @NotBlank(message = "Config key is required")
    private String configKey;

    @NotBlank(message = "Config value is required")
    private String configValue;

    private String description;

    private String category;

    private String type;
}
