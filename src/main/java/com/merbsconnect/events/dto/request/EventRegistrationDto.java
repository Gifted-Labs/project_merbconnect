package com.merbsconnect.events.dto.request;

import com.merbsconnect.enums.AcademicLevel;
import com.merbsconnect.enums.ReferralSource;
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
    private String university;

    /**
     * Department or faculty (optional)
     */
    private String department;

    /**
     * How the registrant heard about the event
     */
    private ReferralSource referralSource;

    /**
     * If referralSource is OTHER, this field stores the custom source
     */
    private String referralSourceOther;

    /**
     * Student ID (optional, for verification purposes)
     */
    private String studentId;

    /**
     * Dietary restrictions or special requirements
     */
    private String dietaryRestrictions;

    /**
     * Emergency contact name
     */
    private String emergencyContactName;

    /**
     * Emergency contact phone
     */
    private String emergencyContactPhone;
}
