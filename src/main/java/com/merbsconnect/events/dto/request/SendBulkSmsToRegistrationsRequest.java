package com.merbsconnect.events.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendBulkSmsToRegistrationsRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotEmpty(message = "At least one registration ID must be selected")
    private List<String> selectedEmails; // List of emails to send SMS to

    @NotNull(message = "Message is required")
    @Size(min = 1, max = 1600, message = "Message must be between 1 and 1600 characters")
    private String message;
}

