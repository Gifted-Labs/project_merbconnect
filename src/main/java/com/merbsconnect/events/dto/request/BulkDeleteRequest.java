package com.merbsconnect.events.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for bulk deletion of registrations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkDeleteRequest {

    @NotEmpty(message = "Registration emails list cannot be empty")
    private List<String> registrationEmails;
}
