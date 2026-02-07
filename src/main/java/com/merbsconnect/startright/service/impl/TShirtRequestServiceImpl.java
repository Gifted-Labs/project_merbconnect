package com.merbsconnect.startright.service.impl;

import com.merbsconnect.startright.dto.request.TShirtRequestDto;
import com.merbsconnect.startright.dto.response.TShirtDailyAnalyticsDto;
import com.merbsconnect.startright.dto.response.TShirtDashboardDto;
import com.merbsconnect.startright.dto.response.TShirtRequestResponseDto;
import com.merbsconnect.startright.entity.TShirtRequest;
import com.merbsconnect.startright.enums.RequestStatus;
import com.merbsconnect.enums.ShirtColor;
import com.merbsconnect.enums.ShirtSize;
import com.merbsconnect.startright.repository.TShirtRequestRepository;
import com.merbsconnect.startright.service.TShirtRequestService;
import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TShirtRequestServiceImpl implements TShirtRequestService {

    private final TShirtRequestRepository repository;
    private final EmailService emailService;
    private final SmsService smsService;

    @Value("${app.admin.phone}")
    private String adminPhone;

    @Value("${app.admin.email}")
    private String adminEmail;

    // Configurable price per shirt (can be moved to application.properties later)
    private static final BigDecimal PRICE_PER_SHIRT = new BigDecimal("50.00");

    @Override
    @Transactional
    public TShirtRequestResponseDto submitRequest(TShirtRequestDto requestDto) {
        TShirtRequest request = TShirtRequest.builder()
                .fullName(requestDto.getFullName())
                .email(requestDto.getEmail())
                .phoneNumber(requestDto.getPhoneNumber())
                .tShirtColor(requestDto.getTShirtColor())
                .tShirtSize(requestDto.getTShirtSize())
                .quantity(requestDto.getQuantity())
                .requestStatus(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        TShirtRequest savedRequest = repository.save(request);

        // Send Notifications
        try {
            // SMS to User
            String userSms = String.format("Hello %s, your request for %dx %s (%s) T-Shirt has been received. We will contact you shortly regarding payment.",
                    request.getFullName(), request.getQuantity(), request.getTShirtColor(), request.getTShirtSize());
            
            smsService.sendBulkSms(BulkSmsRequest.builder()
                    .sender("StartRight")
                    .recipients(List.of(request.getPhoneNumber()))
                    .message(userSms)
                    .build());

            // SMS to Admin
            String adminSms = String.format("New T-Shirt Request:\n%s\n%s\n%dx %s %s\nPhone: %s",
                    request.getFullName(), request.getEmail(), request.getQuantity(), request.getTShirtColor(), request.getTShirtSize(), request.getPhoneNumber());

            smsService.sendBulkSms(BulkSmsRequest.builder()
                    .sender("StartRight")
                    .recipients(List.of(adminPhone))
                    .message(adminSms)
                    .build());

            // Email to Admin
            emailService.sendTshirtOrderAdminEmail(
                    request.getFullName(),
                    request.getEmail(),
                    request.getPhoneNumber(),
                    request.getTShirtSize().toString(),
                    "Start Right Conference 2026"
            );

        } catch (Exception e) {
            log.error("Failed to send T-Shirt request notifications", e);
        }

        return mapToResponseDto(savedRequest);
    }

    @Override
    public List<TShirtRequestResponseDto> getAllRequests(RequestStatus status) {
        List<TShirtRequest> requests;
        if (status != null) {
            requests = repository.findByRequestStatusOrderByCreatedAtDesc(status);
        } else {
            requests = repository.findAllByOrderByCreatedAtDesc();
        }
        return requests.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TShirtRequestResponseDto updateRequestStatus(Long requestId, RequestStatus status) {
        TShirtRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        request.setRequestStatus(status);
        TShirtRequest updatedRequest = repository.save(request);
        return mapToResponseDto(updatedRequest);
    }

    @Override
    public TShirtRequestResponseDto getRequestById(Long requestId) {
        TShirtRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));
        return mapToResponseDto(request);
    }

    @Override
    public List<TShirtRequestResponseDto> getAllRequestsForExport() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TShirtDailyAnalyticsDto> getDailyAnalytics() {
        List<Object[]> rawData = repository.getDailyAnalytics();
        return rawData.stream()
                .map(row -> {
                    LocalDate date;
                    Object dateObj = row[0];
                    if (dateObj instanceof java.sql.Date) {
                        date = ((java.sql.Date) dateObj).toLocalDate();
                    } else if (dateObj instanceof LocalDate) {
                        date = (LocalDate) dateObj;
                    } else {
                        date = LocalDate.parse(dateObj.toString());
                    }
                    return TShirtDailyAnalyticsDto.builder()
                            .date(date)
                            .totalRequests(((Number) row[1]).longValue())
                            .totalQuantity(((Number) row[2]).intValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public TShirtDashboardDto getDashboardMetrics() {
        Long totalRequests = repository.count();
        Integer totalShirts = repository.sumAllQuantities();
        Long pendingCount = repository.countByRequestStatus(RequestStatus.PENDING);
        Long completedCount = repository.countByRequestStatus(RequestStatus.COMPLETED);
        Long declinedCount = repository.countByRequestStatus(RequestStatus.DECLINED);

        BigDecimal totalRevenue = PRICE_PER_SHIRT.multiply(new BigDecimal(totalShirts));

        return TShirtDashboardDto.builder()
                .totalRequests(totalRequests)
                .totalShirtsRequested(totalShirts)
                .totalRevenue(totalRevenue)
                .pricePerShirt(PRICE_PER_SHIRT)
                .pendingRequests(pendingCount)
                .completedRequests(completedCount)
                .declinedRequests(declinedCount)
                .build();
    }

    private TShirtRequestResponseDto mapToResponseDto(TShirtRequest request) {
        return TShirtRequestResponseDto.builder()
                .requestId(request.getRequestId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .tShirtColor(request.getTShirtColor())
                .tShirtSize(request.getTShirtSize())
                .quantity(request.getQuantity())
                .requestStatus(request.getRequestStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
