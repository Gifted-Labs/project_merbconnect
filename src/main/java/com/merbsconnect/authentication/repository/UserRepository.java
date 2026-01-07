package com.merbsconnect.authentication.repository;

import com.merbsconnect.authentication.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Optional<User> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    boolean existsByPhoneNumber(String phoneNumber);

    // Admin management methods
    Page<User> findByStatus(com.merbsconnect.enums.UserStatus status, Pageable pageable);

    long countByStatus(com.merbsconnect.enums.UserStatus status);

    Page<User> findByRole(com.merbsconnect.enums.UserRole role, Pageable pageable);
}
