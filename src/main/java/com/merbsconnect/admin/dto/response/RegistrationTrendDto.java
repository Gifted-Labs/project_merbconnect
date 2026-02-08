package com.merbsconnect.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for registration trend data points.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationTrendDto {
    private String period; // e.g., "2024-01", "Jan 2024"
    private Long registrations;
}
