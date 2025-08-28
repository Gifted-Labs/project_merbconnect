package com.merbsconnect.sms.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTemplateRequest {

    @Valid
    @NotNull(message = "Title is required")
    private String title;

    @Valid
    @NotNull(message = "Content is required" )
    @Size(min = 5, message = "Content must be more than 5 characters")
    private String content;
}
