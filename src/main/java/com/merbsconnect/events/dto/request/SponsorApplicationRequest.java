package com.merbsconnect.events.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SponsorApplicationRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Sponsorship level is required")
    private String sponsorshipLevel;

    private String message;
}
