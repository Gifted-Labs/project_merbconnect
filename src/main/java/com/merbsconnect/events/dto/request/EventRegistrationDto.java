package com.merbsconnect.events.dto.request;

import com.merbsconnect.enums.ShirtSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationDto {

    private String name;
    private String email;
    private String phone;
    private String note;

    /**
     * Whether the participant needs a t-shirt.
     */
    private Boolean needsShirt;

    /**
     * Shirt size if needsShirt is true.
     */
    private ShirtSize shirtSize;
}
