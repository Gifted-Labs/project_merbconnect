package com.merbsconnect.events.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class VolunteerApplicationRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Motivation is required")
    private String motivation;

    @NotBlank(message = "Area of interest is required")
    private String areaOfInterest;
}
