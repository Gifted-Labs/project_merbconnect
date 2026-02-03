package com.merbsconnect.events.model;

import com.merbsconnect.enums.AcademicLevel;
import com.merbsconnect.enums.ReferralSource;
import com.merbsconnect.enums.University;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Registration {

    private String email;

    private String name;

    private String phone;

    private String note;

    private String program;

    @Enumerated(EnumType.STRING)
    private AcademicLevel academicLevel;

    @Enumerated(EnumType.STRING)
    private University university;

    @Enumerated(EnumType.STRING)
    private ReferralSource referralSource;

    private String referralSourceOther;
}
