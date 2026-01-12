package com.merbsconnect.events.repository;

import com.merbsconnect.events.model.EventRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for enhanced event registrations with QR code and check-in
 * support.
 */
@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    /**
     * Find all registrations for an event with pagination.
     */
    Page<EventRegistration> findByEventId(Long eventId, Pageable pageable);

    /**
     * Find registration by unique token (for QR code scanning).
     */
    Optional<EventRegistration> findByRegistrationToken(String registrationToken);

    /**
     * Find registration by event and email.
     */
    Optional<EventRegistration> findByEventIdAndEmail(Long eventId, String email);

    /**
     * Check if email is already registered for an event.
     */
    boolean existsByEventIdAndEmail(Long eventId, String email);

    /**
     * Count checked-in registrations for an event.
     */
    long countByEventIdAndCheckedIn(Long eventId, boolean checkedIn);

    /**
     * Count total registrations for an event.
     */
    long countByEventId(Long eventId);
}
