package com.merbsconnect.startright.repository;

import com.merbsconnect.startright.entity.TShirtRequest;
import com.merbsconnect.startright.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TShirtRequestRepository extends JpaRepository<TShirtRequest, Long> {

    List<TShirtRequest> findByRequestStatus(RequestStatus status);

    List<TShirtRequest> findAllByOrderByCreatedAtDesc();

    List<TShirtRequest> findByRequestStatusOrderByCreatedAtDesc(RequestStatus status);

    // Count requests by status
    Long countByRequestStatus(RequestStatus status);

    // Sum of all quantities
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM TShirtRequest t")
    Integer sumAllQuantities();

    // Daily analytics - group by date using native query for PostgreSQL
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as total_requests, COALESCE(SUM(quantity), 0) as total_quantity "
            +
            "FROM tshirt_requests " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY DATE(created_at) DESC", nativeQuery = true)
    List<Object[]> getDailyAnalytics();
}
