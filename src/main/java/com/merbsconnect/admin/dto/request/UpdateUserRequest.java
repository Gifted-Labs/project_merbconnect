package com.merbsconnect.admin.dto.request;

import com.merbsconnect.enums.UserRole;
import com.merbsconnect.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    private String firstName;
    private String lastName;

    @Email(message = "Email must be valid")
    private String email;

    private String password;

    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
    private Boolean isEnabled;
}
