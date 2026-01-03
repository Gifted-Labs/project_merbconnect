package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.Quiz;
import com.merbsconnect.enums.QuizType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz> {
    
    // Basic filtering by quiz type
    Page<Quiz> findByQuizType(QuizType quizType, Pageable pageable);
    
    // Year filtering
    Page<Quiz> findByYearGiven(int yearGiven, Pageable pageable);
    
    // Combined quiz type and year
    Page<Quiz> findByQuizTypeAndYearGiven(QuizType quizType, int yearGiven, Pageable pageable);
    
    // Course filtering with quiz type and/or year
    Page<Quiz> findByCourseIdAndQuizType(Long courseId, QuizType quizType, Pageable pageable);
    
    Page<Quiz> findByCourseIdAndYearGiven(Long courseId, int yearGiven, Pageable pageable);
    
    Page<Quiz> findByCourseIdAndQuizTypeAndYearGiven(Long courseId, QuizType quizType, int yearGiven, Pageable pageable);
    
    // Department filtering with quiz type and/or year
    @Query("SELECT q FROM Quiz q WHERE q.course.department.id = :departmentId AND q.quizType = :quizType")
    Page<Quiz> findByDepartmentIdAndQuizType(Long departmentId, QuizType quizType, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.department.id = :departmentId AND q.yearGiven = :yearGiven")
    Page<Quiz> findByDepartmentIdAndYearGiven(Long departmentId, int yearGiven, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.department.id = :departmentId AND q.quizType = :quizType AND q.yearGiven = :yearGiven")
    Page<Quiz> findByDepartmentIdAndQuizTypeAndYearGiven(Long departmentId, QuizType quizType, int yearGiven, Pageable pageable);
    
    // Faculty filtering with quiz type and/or year
    @Query("SELECT q FROM Quiz q WHERE q.course.department.faculty.id = :facultyId AND q.quizType = :quizType")
    Page<Quiz> findByFacultyIdAndQuizType(Long facultyId, QuizType quizType, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.department.faculty.id = :facultyId AND q.yearGiven = :yearGiven")
    Page<Quiz> findByFacultyIdAndYearGiven(Long facultyId, int yearGiven, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.department.faculty.id = :facultyId AND q.quizType = :quizType AND q.yearGiven = :yearGiven")
    Page<Quiz> findByFacultyIdAndQuizTypeAndYearGiven(Long facultyId, QuizType quizType, int yearGiven, Pageable pageable);
    
    // College filtering with quiz type and/or year
    @Query("SELECT q FROM Quiz q WHERE q.course.department.faculty.college.id = :collegeId AND q.quizType = :quizType")
    Page<Quiz> findByCollegeIdAndQuizType(Long collegeId, QuizType quizType, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.department.faculty.college.id = :collegeId AND q.yearGiven = :yearGiven")
    Page<Quiz> findByCollegeIdAndYearGiven(Long collegeId, int yearGiven, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.department.faculty.college.id = :collegeId AND q.quizType = :quizType AND q.yearGiven = :yearGiven")
    Page<Quiz> findByCollegeIdAndQuizTypeAndYearGiven(Long collegeId, QuizType quizType, int yearGiven, Pageable pageable);
    
    // Program filtering with quiz type and/or year
    @Query("SELECT q FROM Quiz q WHERE q.course.id IN " +
           "(SELECT c.id FROM Course c JOIN c.programs p WHERE p.id = :programId) AND q.quizType = :quizType")
    Page<Quiz> findByProgramIdAndQuizType(Long programId, QuizType quizType, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.id IN " +
           "(SELECT c.id FROM Course c JOIN c.programs p WHERE p.id = :programId) AND q.yearGiven = :yearGiven")
    Page<Quiz> findByProgramIdAndYearGiven(Long programId, int yearGiven, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.id IN " +
           "(SELECT c.id FROM Course c JOIN c.programs p WHERE p.id = :programId) AND q.quizType = :quizType AND q.yearGiven = :yearGiven")
    Page<Quiz> findByProgramIdAndQuizTypeAndYearGiven(Long programId, QuizType quizType, int yearGiven, Pageable pageable);
    
    // Difficulty level filtering
    Page<Quiz> findByDifficultyLevel(String difficultyLevel, Pageable pageable);
    
    Page<Quiz> findByDifficultyLevelAndQuizType(String difficultyLevel, QuizType quizType, Pageable pageable);
    
    // Get available years for filtering UI
    @Query("SELECT DISTINCT q.yearGiven FROM Quiz q ORDER BY q.yearGiven DESC")
    List<Integer> findDistinctYearGiven();
    
    @Query("SELECT DISTINCT q.yearGiven FROM Quiz q WHERE q.quizType = :quizType ORDER BY q.yearGiven DESC")
    List<Integer> findDistinctYearGivenByQuizType(QuizType quizType);
    
    @Query("SELECT DISTINCT q.yearGiven FROM Quiz q WHERE q.course.id = :courseId ORDER BY q.yearGiven DESC")
    List<Integer> findDistinctYearGivenByCourseId(Long courseId);
}