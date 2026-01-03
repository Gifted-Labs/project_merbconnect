package com.merbsconnect.sms.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantSmsRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Template ID is required")
    private Long templateId;

    private List<Long> participantIds; // If empty, send to all participants
}