package com.merbsconnect.sms.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BulkSmsRequest {

    @NotEmpty(message = "Recipient list cannot be empty")
    @JsonProperty("recipient")
    private List<String> recipients;


    @JsonProperty("sender")
    private String sender;

    @NotNull(message = "Message is required")
    @Size(min = 1, max = 1600, message = "Message must be between 1 and 1600 characters")
    @JsonProperty("message")
    private String message;

    @JsonProperty("is_schedule")
    private boolean isScheduled = false;

    @JsonProperty("schedule_date")
    private String scheduleDate; // Format: "YYYY-MM-DD HH:MM:SS"
}
