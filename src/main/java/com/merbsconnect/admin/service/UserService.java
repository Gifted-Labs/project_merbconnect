package com.merbsconnect.admin.service;

import com.merbsconnect.admin.dto.request.CreateUserRequest;
import com.merbsconnect.admin.dto.request.UpdateUserRequest;
import com.merbsconnect.admin.dto.response.UserActivityResponse;
import com.merbsconnect.admin.dto.response.UserResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for user management operations.
 */
public interface UserService {

    /**
     * Get all users with pagination.
     *
     * @param pageable Pagination information
     * @return Page of user responses
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Get user by ID.
     *
     * @param id User ID
     * @return Optional user response
     */
    Optional<UserResponse> getUserById(Long id);

    /**
     * Create a new user.
     *
     * @param request User creation request
     * @return Created user response
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Update an existing user.
     *
     * @param id      User ID
     * @param request User update request
     * @return Updated user response
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Delete a user (soft delete).
     *
     * @param id User ID
     * @return Success message
     */
    MessageResponse deleteUser(Long id);

    /**
     * Get user activity logs.
     *
     * @param id       User ID
     * @param pageable Pagination information
     * @return User activity response
     */
    UserActivityResponse getUserActivity(Long id, Pageable pageable);
}
