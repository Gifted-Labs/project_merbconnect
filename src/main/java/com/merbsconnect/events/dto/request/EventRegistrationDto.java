package com.merbsconnect.events.dto.request;

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


}
