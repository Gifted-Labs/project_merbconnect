package com.merbsconnect.startright.dto.response;

import com.merbsconnect.startright.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TShirtRequestResponseDto {

    private Long requestId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String tShirtColor;
    private String tShirtSize;
    private Integer quantity;
    private RequestStatus requestStatus;
    private LocalDateTime createdAt;
}
