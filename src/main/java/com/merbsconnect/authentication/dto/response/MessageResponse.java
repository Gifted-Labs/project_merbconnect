package com.merbsconnect.authentication.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic response message")
public class MessageResponse {

    @Schema(
            description = "Response message describing the result of the operation",
            example = "Email verified successfully!"
    )
    private String message;
}