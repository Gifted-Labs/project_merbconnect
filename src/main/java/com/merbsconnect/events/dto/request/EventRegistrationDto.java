package com.merbsconnect.events.dto.request;

import com.merbsconnect.enums.AcademicLevel;
import com.merbsconnect.enums.ReferralSource;
import com.merbsconnect.enums.ShirtSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
     * Legacy: Simple shirt size field.
     * Kept for backward compatibility.
     * New registrations should use merchandiseOrders instead.
     */
    private ShirtSize shirtSize;

    /**
     * Detailed merchandise orders (color, size, quantity).
     * Use this instead of simple shirtSize for new registrations.
     */
    private List<MerchandiseOrderDto> merchandiseOrders;

    // ===== University Student Information =====

    /**
     * The program/course the student is studying (e.g., "Computer Science",
     * "Medicine")
     */
    private String program;

    /**
     * The academic level/year of the student
     */
    private AcademicLevel academicLevel;

    /**
     * The name of the university/institution
     */
    private com.merbsconnect.enums.University university;

    /**
     * How the registrant heard about the event
     */
    private ReferralSource referralSource;

    /**
     * If referralSource is OTHER, this field stores the custom source
     */
    private String referralSourceOther;
}
