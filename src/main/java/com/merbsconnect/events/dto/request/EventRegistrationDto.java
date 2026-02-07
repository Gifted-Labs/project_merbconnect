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

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String note;
    private Boolean needsShirt;
    private ShirtSize shirtSize;
    private List<MerchandiseOrderDto> merchandiseOrders;
    private String program;
    private AcademicLevel academicLevel;
    private com.merbsconnect.enums.University university;
    private ReferralSource referralSource;
    private String referralSourceOther;
}
