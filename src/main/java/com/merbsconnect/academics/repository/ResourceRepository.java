package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {
    
    // Basic course filtering
    Page<Resource> findByCourseId(Long courseId, Pageable pageable);
    
    // Department filtering
    @Query("SELECT r FROM Resource r WHERE r.course.department.id = :departmentId")
    Page<Resource> findByDepartmentId(Long departmentId, Pageable pageable);
    
    // Faculty filtering
    @Query("SELECT r FROM Resource r WHERE r.course.department.faculty.id = :facultyId")
    Page<Resource> findByFacultyId(Long facultyId, Pageable pageable);
    
    // College filtering
    @Query("SELECT r FROM Resource r WHERE r.course.department.faculty.college.id = :collegeId")
    Page<Resource> findByCollegeId(Long collegeId, Pageable pageable);
    
    // Program filtering
    @Query("SELECT r FROM Resource r WHERE r.course.id IN " +
           "(SELECT c.id FROM Course c JOIN c.programs p WHERE p.id = :programId)")
    Page<Resource> findByProgramId(Long programId, Pageable pageable);
    
    // Resource type filtering
    @Query("SELECT r FROM Resource r WHERE TYPE(r) = :resourceType")
    Page<Resource> findByResourceType(Class resourceType, Pageable pageable);
    
    // Combined filters
    @Query("SELECT r FROM Resource r WHERE TYPE(r) = :resourceType AND r.course.id = :courseId")
    Page<Resource> findByResourceTypeAndCourseId(Class resourceType, Long courseId, Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE TYPE(r) = :resourceType AND r.course.department.id = :departmentId")
    Page<Resource> findByResourceTypeAndDepartmentId(Class resourceType, Long departmentId, Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE TYPE(r) = :resourceType AND r.course.department.faculty.id = :facultyId")
    Page<Resource> findByResourceTypeAndFacultyId(Class resourceType, Long facultyId, Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE TYPE(r) = :resourceType AND r.course.department.faculty.college.id = :collegeId")
    Page<Resource> findByResourceTypeAndCollegeId(Class resourceType, Long collegeId, Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE TYPE(r) = :resourceType AND r.course.id IN " +
           "(SELECT c.id FROM Course c JOIN c.programs p WHERE p.id = :programId)")
    Page<Resource> findByResourceTypeAndProgramId(Class resourceType, Long programId, Pageable pageable);
    
    // Search by title with filters
    @Query("SELECT r FROM Resource r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND r.course.id = :courseId")
    Page<Resource> searchByTitleAndCourseId(String keyword, Long courseId, Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND TYPE(r) = :resourceType")
    Page<Resource> searchByTitleAndResourceType(String keyword, Class resourceType, Pageable pageable);
}