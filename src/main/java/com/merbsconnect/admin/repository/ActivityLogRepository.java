package com.merbsconnect.admin.repository;

import com.merbsconnect.admin.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository for ActivityLog entity.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);

    Page<ActivityLog> findByTimestampAfter(LocalDateTime timestamp, Pageable pageable);
}
