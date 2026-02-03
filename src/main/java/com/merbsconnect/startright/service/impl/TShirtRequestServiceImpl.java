package com.merbsconnect.startright.service.impl;

import com.merbsconnect.startright.dto.request.TShirtRequestDto;
import com.merbsconnect.startright.dto.response.TShirtRequestResponseDto;
import com.merbsconnect.startright.entity.TShirtRequest;
import com.merbsconnect.startright.enums.RequestStatus;
import com.merbsconnect.startright.repository.TShirtRequestRepository;
import com.merbsconnect.startright.service.TShirtRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TShirtRequestServiceImpl implements TShirtRequestService {

    private final TShirtRequestRepository repository;

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
