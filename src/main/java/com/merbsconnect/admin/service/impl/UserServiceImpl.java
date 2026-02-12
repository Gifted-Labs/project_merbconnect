package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.request.CreateUserRequest;
import com.merbsconnect.admin.dto.request.UpdateUserRequest;
import com.merbsconnect.admin.dto.response.UserActivityResponse;
import com.merbsconnect.admin.dto.response.UserResponse;
import com.merbsconnect.admin.model.ActivityLog;
import com.merbsconnect.admin.repository.ActivityLogRepository;
import com.merbsconnect.admin.service.AuditService;
import com.merbsconnect.admin.service.AuditService;
import com.merbsconnect.admin.service.UserService;
import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.sms.service.SmsService;
import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.enums.UserStatus;
import com.merbsconnect.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserService for user management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        return userRepository.findAll(pageable)
                .map(this::mapToUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("User with email " + request.getEmail() + " already exists");
        }

        // Validate phone uniqueness
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("User with phone number " + request.getPhoneNumber() + " already exists");
        }

        // Create user entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .isEnabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Log audit trail
        auditService.logAction("CREATE", "User", savedUser.getId(),
                "Created user: " + savedUser.getEmail());

        log.info("Successfully created user with ID: {}", savedUser.getId());

        // Send email notification with credentials
        try {
            emailService.sendAccountCreatedEmail(
                    savedUser.getFirstName() + " " + savedUser.getLastName(),
                    savedUser.getEmail(),
                    request.getPassword());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", savedUser.getEmail(), e.getMessage());
        }

        // Send SMS notification
        try {
            String smsMessage = String.format(
                    "Hello %s, your admin account for MerbsConnect has been created. CHECK YOUR EMAIL for login credentials.",
                    savedUser.getFirstName());

            BulkSmsRequest smsRequest = BulkSmsRequest.builder()
                    .recipients(List.of(savedUser.getPhoneNumber()))
                    .message(smsMessage)
                    .sender("MerbsConn") // Adjust sender ID if needed
                    .build();

            smsService.sendBulkSmsAsync(smsRequest);
        } catch (Exception e) {
            log.error("Failed to send welcome SMS to {}: {}", savedUser.getPhoneNumber(), e.getMessage());
        }

        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with ID: " + id));

        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new BusinessException("Phone number already in use: " + request.getPhoneNumber());
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getIsEnabled() != null) {
            user.setEnabled(request.getIsEnabled());
        }

        User updatedUser = userRepository.save(user);

        // Log audit trail
        auditService.logAction("UPDATE", "User", updatedUser.getId(),
                "Updated user: " + updatedUser.getEmail());

        log.info("Successfully updated user with ID: {}", id);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public MessageResponse deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with ID: " + id));

        // Soft delete - set status to DELETED
        user.setStatus(UserStatus.DELETED);
        user.setEnabled(false);
        userRepository.save(user);

        // Log audit trail
        auditService.logAction("DELETE", "User", id,
                "Soft deleted user: " + user.getEmail());

        log.info("Successfully deleted user with ID: {}", id);
        return MessageResponse.builder()
                .message("User successfully deleted")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserActivityResponse getUserActivity(Long id, Pageable pageable) {
        log.info("Fetching activity for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with ID: " + id));

        Page<ActivityLog> activityLogs = activityLogRepository.findByUserId(id, pageable);

        List<UserActivityResponse.ActivityLogDto> activities = activityLogs.stream()
                .map(log -> UserActivityResponse.ActivityLogDto.builder()
                        .activityType(log.getActivityType())
                        .endpoint(log.getEndpoint())
                        .timestamp(log.getTimestamp())
                        .details(log.getDetails())
                        .build())
                .collect(Collectors.toList());

        return UserActivityResponse.builder()
                .userId(user.getId())
                .username(user.getFirstName() + " " + user.getLastName())
                .activities(activities)
                .build();
    }

    /**
     * Map User entity to UserResponse DTO.
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .isEnabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
