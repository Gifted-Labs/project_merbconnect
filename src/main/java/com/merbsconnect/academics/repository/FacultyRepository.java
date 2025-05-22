package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.College;
import com.merbsconnect.academics.domain.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    List<Faculty> findByCollege(College college);

    List<Faculty> findByCollegeId(Long collegeId);

    Optional<Faculty> findByFacultyNameAndCollegeId(String facultyName, Long collegeId);

    boolean existsByFacultyNameAndCollegeId(String facultyName, Long collegeId);

    List<Faculty> findByFacultyNameIgnoreCaseContaining(String facultyName);

    @Query("SELECT f FROM Faculty f LEFT JOIN FETCH f.departments WHERE f.id = :id")
    Optional<Faculty> findByIdWithDepartments(Long id);

    @Query("SELECT f FROM Faculty f LEFT JOIN FETCH f.departments WHERE f.college.id = :collegeId")
    List<Faculty> findByCollegeIdWithDepartments(Long collegeId);
}
