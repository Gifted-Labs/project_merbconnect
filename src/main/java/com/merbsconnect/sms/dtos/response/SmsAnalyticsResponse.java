package com.merbsconnect.sms.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsAnalyticsResponse {
    
    private Long totalSent;
    private Long totalDelivered;
    private Long totalFailed;
    private Long totalPending;
    private Double totalCost;
    private Double deliveryRate;
    private LocalDateTime reportGeneratedAt;
    
    public static SmsAnalyticsResponse empty() {
        return SmsAnalyticsResponse.builder()
                .totalSent(0L)
                .totalDelivered(0L)
                .totalFailed(0L)
                .totalPending(0L)
                .totalCost(0.0)
                .deliveryRate(0.0)
                .reportGeneratedAt(LocalDateTime.now())
                .build();
    }
}