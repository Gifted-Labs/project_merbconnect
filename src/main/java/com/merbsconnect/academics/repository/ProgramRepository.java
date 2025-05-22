package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.Department;
import com.merbsconnect.academics.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    List<Program> findByDepartment(Department department);

    List<Program> findByDepartmentId(Long departmentId);

    List<Program> findByProgramNameContainingIgnoreCase(String programName);

    Optional<Program> findByProgramCode(String programCode);

    Optional<Program> findByProgramNameAndDepartmentId(String programName, Long departmentId);

    boolean existsByProgramNameAndDepartmentId(String programName, Long departmentId);

    boolean existsByProgramCode(String programCode);

    @Query("SELECT p FROM Program  p WHERE p.department.faculty.id = :facultyId")
    List<Program> findByFacultyId(Long facultyId);

    @Query("SELECT p FROM Program p WHERE p.department.faculty.college.id = :collegeId")
    List<Program> findByCollegeId(Long collegeId);
}
