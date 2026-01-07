package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.request.CreateUserRequest;
import com.merbsconnect.admin.dto.request.UpdateUserRequest;
import com.merbsconnect.admin.dto.response.UserActivityResponse;
import com.merbsconnect.admin.dto.response.UserResponse;
import com.merbsconnect.admin.model.ActivityLog;
import com.merbsconnect.admin.repository.ActivityLogRepository;
import com.merbsconnect.admin.service.AuditService;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.enums.UserRole;
import com.merbsconnect.enums.UserStatus;
import com.merbsconnect.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .password("encodedPassword")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .isEnabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        createRequest = CreateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+9876543210")
                .password("password123")
                .role(UserRole.ROLE_USER)
                .build();

        updateRequest = UpdateUserRequest.builder()
                .firstName("John Updated")
                .role(UserRole.ROLE_ADMIN)
                .build();
    }

    @Test
    @DisplayName("getAllUsers should return paginated users")
    void testGetAllUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<UserResponse> result = userService.getAllUsers(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getUserById should return user when exists")
    void testGetUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserResponse> result = userService.getUserById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("createUser should create user successfully")
    void testCreateUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(auditService).logAction(anyString(), anyString(), any(), anyString());

        // Act
        UserResponse result = userService.createUser(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());

        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository).existsByPhoneNumber(createRequest.getPhoneNumber());
        verify(passwordEncoder).encode(createRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(auditService).logAction(eq("CREATE"), eq("User"), any(), anyString());
    }

    @Test
    @DisplayName("createUser should throw exception when email exists")
    void testCreateUserEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser should update user successfully")
    void testUpdateUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(auditService).logAction(anyString(), anyString(), any(), anyString());

        // Act
        UserResponse result = userService.updateUser(1L, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(testUser.getFirstName()).isEqualTo("John Updated");
        assertThat(testUser.getRole()).isEqualTo(UserRole.ROLE_ADMIN);

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        verify(auditService).logAction(eq("UPDATE"), eq("User"), eq(1L), anyString());
    }

    @Test
    @DisplayName("updateUser should throw exception when user not found")
    void testUpdateUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(999L, updateRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not found");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteUser should soft delete user")
    void testDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(auditService).logAction(anyString(), anyString(), any(), anyString());

        // Act
        MessageResponse result = userService.deleteUser(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).contains("successfully deleted");
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(testUser.isEnabled()).isFalse();

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        verify(auditService).logAction(eq("DELETE"), eq("User"), eq(1L), anyString());
    }

    @Test
    @DisplayName("getUserActivity should return user activity")
    void testGetUserActivity() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        ActivityLog activityLog = ActivityLog.builder()
                .id(1L)
                .userId(1L)
                .activityType("LOGIN")
                .endpoint("/api/login")
                .timestamp(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(activityLogRepository.findByUserId(1L, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(activityLog)));

        // Act
        UserActivityResponse result = userService.getUserActivity(1L, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getActivities()).hasSize(1);
        assertThat(result.getActivities().get(0).getActivityType()).isEqualTo("LOGIN");

        verify(userRepository).findById(1L);
        verify(activityLogRepository).findByUserId(1L, pageable);
    }
}
