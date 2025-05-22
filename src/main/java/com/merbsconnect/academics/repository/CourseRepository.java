package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.Course;
import com.merbsconnect.enums.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByDepartmentId(Long departmentId);

    List<Course> findBySemester(Semester semester);

    List<Course> findByDepartmentIdAndSemester(Long departmentId, Semester semester);

    boolean existsByCourseCode(String courseCode);

    @Query("SELECT c FROM Course c WHERE c.department.faculty.id = :facultyId")
    List<Course> findByFacultyId(Long facultyId);

    @Query("SELECT c FROM Course c WHERE c.department.faculty.college.id = :collegeId")
    List<Course> findByCollegeId(Long collegeId);

    @Query("SELECT c FROM Course c JOIN c.programs p WHERE p.id = :programId")
    List<Course> findByProgramsId(Long programId);

    @Query("SELECT DISTINCT c.semester FROM Course c")
    List<Semester> findDistinctSemesters();

}
