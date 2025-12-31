package com.merbsconnect.authentication.dto.request;

import com.merbsconnect.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user registration requests.
 * This class encapsulates the data required from clients during the registration process,
 * separating the API layer from the entity model.
 *
 * Lombok annotations reduce boilerplate code by automatically generating
 * constructors, getters, setters, equals, hashCode, and toString methods.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request payload")
public class RegistrationRequest {

    /**
     * User's first name
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(
            description = "User's first name",
            example = "John",
            minLength = 2,
            maxLength = 50
    )
    private String firstName;

    /**
     * User's last name
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(
            description = "User's last name",
            example = "Doe",
            minLength = 2,
            maxLength = 50
    )
    private String lastName;

    /**
     * User's email address, serves as a natural identifier
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(
            description = "User's email address (must be unique)",
            example = "john.doe@example.com",
            format = "email"
    )
    private String email;

    /**
     * User's password (will be encrypted before storage)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character")
    @Schema(
            description = "User's password. Must contain at least 8 characters with uppercase, lowercase, digit, and special character",
            example = "SecurePass123!",
            minLength = 8
    )
    private String password;

    /**
     * User's phone number, serves as a natural identifier
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;

    /**
     * User's role in the system
     */
    private UserRole role;
}
