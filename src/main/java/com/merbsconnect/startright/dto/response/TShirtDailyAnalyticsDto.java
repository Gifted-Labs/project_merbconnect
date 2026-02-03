package com.merbsconnect.startright.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TShirtDailyAnalyticsDto {
    private LocalDate date;
    private Long totalRequests;
    private Integer totalQuantity;
}
