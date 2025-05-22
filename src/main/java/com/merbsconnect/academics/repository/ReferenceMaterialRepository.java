package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.ReferenceMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferenceMaterialRepository extends JpaRepository<ReferenceMaterial, Long>, JpaSpecificationExecutor<ReferenceMaterial> {
    
    // Format filtering
    Page<ReferenceMaterial> findByFormat(String format, Pageable pageable);
    
    // Course filtering with format
    Page<ReferenceMaterial> findByCourseIdAndFormat(Long courseId, String format, Pageable pageable);
    
    // Department filtering with format
    @Query("SELECT r FROM ReferenceMaterial r WHERE r.course.department.id = :departmentId AND " +
           "(:format IS NULL OR r.format = :format)")
    Page<ReferenceMaterial> findByDepartmentIdAndFormat(Long departmentId, String format, Pageable pageable);
    
    // Faculty filtering with format
    @Query("SELECT r FROM ReferenceMaterial r WHERE r.course.department.faculty.id = :facultyId AND " +
           "(:format IS NULL OR r.format = :format)")
    Page<ReferenceMaterial> findByFacultyIdAndFormat(Long facultyId, String format, Pageable pageable);
    
    // College filtering with format
    @Query("SELECT r FROM ReferenceMaterial r WHERE r.course.department.faculty.college.id = :collegeId AND " +
           "(:format IS NULL OR r.format = :format)")
    Page<ReferenceMaterial> findByCollegeIdAndFormat(Long collegeId, String format, Pageable pageable);
    
    // Program filtering with format
    @Query("SELECT r FROM ReferenceMaterial r WHERE r.course.id IN " +
           "(SELECT c.id FROM Course c JOIN c.programs p WHERE p.id = :programId) AND " +
           "(:format IS NULL OR r.format = :format)")
    Page<ReferenceMaterial> findByProgramIdAndFormat(Long programId, String format, Pageable pageable);
    
    // Author filtering
    Page<ReferenceMaterial> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    // Publication year filtering
    Page<ReferenceMaterial> findByPublicationYear(String publicationYear, Pageable pageable);
    
    // Language filtering
    Page<ReferenceMaterial> findByLanguage(String language, Pageable pageable);
    
    // Combined filters
    @Query("SELECT r FROM ReferenceMaterial r WHERE " +
           "(:courseId IS NULL OR r.course.id = :courseId) AND " +
           "(:format IS NULL OR r.format = :format) AND " +
           "(:language IS NULL OR r.language = :language) AND " +
           "(:publicationYear IS NULL OR r.publicationYear = :publicationYear) AND " +
           "(:author IS NULL OR LOWER(r.author) LIKE LOWER(CONCAT('%', :author, '%')))")
    Page<ReferenceMaterial> findWithMultipleFilters(
            Long courseId, String format, String language, 
            String publicationYear, String author, Pageable pageable);
    
    // Get available formats for filtering UI
    @Query("SELECT DISTINCT r.format FROM ReferenceMaterial r WHERE r.format IS NOT NULL")
    List<String> findDistinctFormats();
    
    // Get available languages for filtering UI
    @Query("SELECT DISTINCT r.language FROM ReferenceMaterial r WHERE r.language IS NOT NULL")
    List<String> findDistinctLanguages();
    
    // Get available publication years for filtering UI
    @Query("SELECT DISTINCT r.publicationYear FROM ReferenceMaterial r WHERE r.publicationYear IS NOT NULL ORDER BY r.publicationYear DESC")
    List<String> findDistinctPublicationYears();
}