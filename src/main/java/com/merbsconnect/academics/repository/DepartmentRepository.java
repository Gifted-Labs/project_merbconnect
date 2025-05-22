package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.Department;
import com.merbsconnect.academics.domain.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByFaculty(Faculty faculty);

    List<Department> findByFacultyId(Long facultyId);

    Optional<Department> findByDepartmentNameAndFacultyId(String departmentName, Long facultyId);

    boolean existsByDepartmentNameAndFacultyId(String departmentName, Long facultyId);

    List<Department> findByDepartmentNameIgnoreCaseContaining(String departmentName);

    @Query("SELECT d FROM Department  d WHERE d.faculty.college.id = :collegeId")
    List<Department> findByCollegeId(Long collegeId);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.programs WHERE d.id = :id")
    List<Department> findByIdWithPrograms(Long id);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.programs WHERE d.faculty.id = :facultyId")
    List<Department> findByFacultyIdWithPrograms(Long facultyId);
}
