package com.merbsconnect.sms.dtos.request;

import com.merbsconnect.sms.domain.SmsTemplateCategory;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 5, max = 1600, message = "Content must be between 5 and 1600 characters")
    private String body;

    @NotNull(message = "Category is required")
    private SmsTemplateCategory category;
}