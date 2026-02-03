package com.merbsconnect.startright.repository;

import com.merbsconnect.startright.entity.TShirtRequest;
import com.merbsconnect.startright.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TShirtRequestRepository extends JpaRepository<TShirtRequest, Long> {

    List<TShirtRequest> findByRequestStatus(RequestStatus status);

    List<TShirtRequest> findAllByOrderByCreatedAtDesc();

    List<TShirtRequest> findByRequestStatusOrderByCreatedAtDesc(RequestStatus status);
}
