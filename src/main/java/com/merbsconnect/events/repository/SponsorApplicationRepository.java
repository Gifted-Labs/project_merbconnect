package com.merbsconnect.events.repository;

import com.merbsconnect.events.model.SponsorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SponsorApplicationRepository extends JpaRepository<SponsorApplication, Long> {
}
