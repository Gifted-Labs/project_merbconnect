package com.merbsconnect.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for active user sessions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSessionResponse {

    private String sessionId;
    private String username;
    private LocalDateTime loginTime;
    private String ipAddress;
    private LocalDateTime lastActivity;
}
