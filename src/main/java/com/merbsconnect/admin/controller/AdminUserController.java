package com.merbsconnect.admin.controller;

import com.merbsconnect.admin.dto.request.CreateUserRequest;
import com.merbsconnect.admin.dto.request.UpdateUserRequest;
import com.merbsconnect.admin.dto.response.UserActivityResponse;
import com.merbsconnect.admin.dto.response.UserResponse;
import com.merbsconnect.admin.service.UserService;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for user management operations.
 * All endpoints require SUPER_ADMIN or ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:5173" }, allowedHeaders = { "*" }, maxAge = 3600)
public class AdminUserController {

    private final UserService userService;

    /**
     * Get all users with pagination.
     * Requires SUPER_ADMIN or ADMIN role.
     *
     * @param pageable Pagination parameters
     * @return Page of users
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        try {
            log.info("Fetching all users with pagination");
            Page<UserResponse> users = userService.getAllUsers(pageable);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get user by ID.
     * Requires SUPER_ADMIN or ADMIN role.
     *
     * @param id User ID
     * @return User response
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        try {
            log.info("Fetching user by ID: {}", id);
            Optional<UserResponse> user = userService.getUserById(id);
            return user.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new user.
     * Requires SUPER_ADMIN role.
     *
     * @param request User creation request
     * @return Created user response
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            log.info("Creating new user with email: {}", request.getEmail());
            UserResponse user = userService.createUser(request);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (BusinessException e) {
            log.error("Business error creating user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing user.
     * Requires SUPER_ADMIN or ADMIN role.
     *
     * @param id      User ID
     * @param request User update request
     * @return Updated user response
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            log.info("Updating user with ID: {}", id);
            UserResponse user = userService.updateUser(id, request);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Business error updating user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a user (soft delete).
     * Requires SUPER_ADMIN role.
     *
     * @param id User ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        try {
            log.info("Deleting user with ID: {}", id);
            MessageResponse response = userService.deleteUser(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Business error deleting user: {}", e.getMessage());
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get user activity logs.
     * Requires SUPER_ADMIN or ADMIN role.
     *
     * @param id       User ID
     * @param pageable Pagination parameters
     * @return User activity response
     */
    @GetMapping("/{id}/activity")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<UserActivityResponse> getUserActivity(
            @PathVariable Long id,
            Pageable pageable) {
        try {
            log.info("Fetching activity for user ID: {}", id);
            UserActivityResponse activity = userService.getUserActivity(id, pageable);
            return new ResponseEntity<>(activity, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Business error fetching user activity: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error fetching user activity: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
