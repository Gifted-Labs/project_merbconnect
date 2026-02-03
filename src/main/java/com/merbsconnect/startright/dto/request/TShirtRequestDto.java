package com.merbsconnect.startright.dto.request;

import com.merbsconnect.enums.ShirtColor;
import com.merbsconnect.enums.ShirtSize;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TShirtRequestDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "T-shirt color is required")
    private ShirtColor tShirtColor;

    @NotNull(message = "T-shirt size is required")
    private ShirtSize tShirtSize;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
