package com.merbsconnect.startright.service;

import com.merbsconnect.startright.dto.request.TShirtRequestDto;
import com.merbsconnect.startright.dto.response.TShirtDailyAnalyticsDto;
import com.merbsconnect.startright.dto.response.TShirtDashboardDto;
import com.merbsconnect.startright.dto.response.TShirtRequestResponseDto;
import com.merbsconnect.startright.enums.RequestStatus;

import java.util.List;

public interface TShirtRequestService {

    TShirtRequestResponseDto submitRequest(TShirtRequestDto requestDto);

    List<TShirtRequestResponseDto> getAllRequests(RequestStatus status);

    TShirtRequestResponseDto updateRequestStatus(Long requestId, RequestStatus status);

    TShirtRequestResponseDto getRequestById(Long requestId);

    // Export all requests
    List<TShirtRequestResponseDto> getAllRequestsForExport();

    // Daily analytics
    List<TShirtDailyAnalyticsDto> getDailyAnalytics();

    // Dashboard metrics
    TShirtDashboardDto getDashboardMetrics();
}
