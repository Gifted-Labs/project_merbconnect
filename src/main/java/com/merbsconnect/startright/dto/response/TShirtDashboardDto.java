package com.merbsconnect.startright.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TShirtDashboardDto {
    private Long totalRequests;
    private Integer totalShirtsRequested;
    private BigDecimal totalRevenue;
    private BigDecimal pricePerShirt;
    private Long pendingRequests;
    private Long completedRequests;
    private Long declinedRequests;
}
