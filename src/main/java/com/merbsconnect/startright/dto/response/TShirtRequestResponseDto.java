package com.merbsconnect.startright.dto.response;

import com.merbsconnect.startright.enums.RequestStatus;
import com.merbsconnect.enums.ShirtColor;
import com.merbsconnect.enums.ShirtSize;
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
    private ShirtColor tShirtColor;
    private ShirtSize tShirtSize;
    private Integer quantity;
    private RequestStatus requestStatus;
    private LocalDateTime createdAt;
}
