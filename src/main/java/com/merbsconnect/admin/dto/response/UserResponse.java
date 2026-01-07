package com.merbsconnect.admin.dto.response;

import com.merbsconnect.enums.UserRole;
import com.merbsconnect.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
    private boolean isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
