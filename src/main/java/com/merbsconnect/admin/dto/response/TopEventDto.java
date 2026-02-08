package com.merbsconnect.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for top events by registration count.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopEventDto {
    private Long id;
    private String title;
    private Long registrationCount;
}
