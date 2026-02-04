package com.merbsconnect.events.dto.request;

import com.merbsconnect.enums.ShirtColor;
import com.merbsconnect.enums.ShirtSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for merchandise order items in event registration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchandiseOrderDto {

    @NotNull(message = "T-shirt color is required")
    private ShirtColor color;

    @NotNull(message = "T-shirt size is required")
    private ShirtSize size;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Builder.Default
    private Integer quantity = 1;
}
