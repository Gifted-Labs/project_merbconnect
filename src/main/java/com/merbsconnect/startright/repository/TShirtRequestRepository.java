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

    // Daily analytics - group by date
    @Query("SELECT CAST(t.createdAt AS LocalDate) as date, COUNT(t) as totalRequests, COALESCE(SUM(t.quantity), 0) as totalQuantity "
            +
            "FROM TShirtRequest t " +
            "GROUP BY CAST(t.createdAt AS LocalDate) " +
            "ORDER BY CAST(t.createdAt AS LocalDate) DESC")
    List<Object[]> getDailyAnalytics();
}
