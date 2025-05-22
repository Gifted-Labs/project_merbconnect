package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    boolean existsByCollegeName(String collegeName);

    Optional<College> findByCollegeName(String collegeName);

    List<College> findAllByCollegeNameIgnoreCaseContaining(String collegeName);

    @Query("SELECT c FROM College c JOIN FETCH c.faculties")
    List<College> findAllWithFaculties();

    @Query("SELECT c FROM College c JOIN FETCH c.faculties WHERE c.id = :id")
    Optional<College> findByIdWithFaculties(Long id);
}
