package com.merbsconnect.events.model;

import com.merbsconnect.enums.ShirtColor;
import com.merbsconnect.enums.ShirtSize;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable entity representing a merchandise order item.
 * Used to store detailed T-shirt orders during event registration.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchandiseOrder {

    @Enumerated(EnumType.STRING)
    private ShirtColor color;

    @Enumerated(EnumType.STRING)
    private ShirtSize size;

    @Min(1)
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Returns a formatted display string for this order.
     */
    public String getDisplayString() {
        return String.format("%s %s (x%d)",
                color != null ? color.getDisplayName() : "N/A",
                size != null ? size.getDisplayName() : "N/A",
                quantity != null ? quantity : 1);
    }
}
