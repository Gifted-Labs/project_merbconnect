package com.merbsconnect.academics.repository;

import com.merbsconnect.academics.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface    QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    // Basic search methods
    Page<Question> findByQuestionTextContainingIgnoreCase(String text, Pageable pageable);
    
    // Resource reference based search
    @Query("SELECT q FROM Question q WHERE q.referencedResource.id = :resourceId")
    Page<Question> findByReferencedResourceId(@Param("resourceId") Long resourceId, Pageable pageable);
    
    // Find questions by tag (assuming we add tags to Question entity)
    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t = :tag")
    Page<Question> findByTag(@Param("tag") String tag, Pageable pageable);
    
    // Find questions by multiple tags
    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t IN :tags GROUP BY q HAVING COUNT(DISTINCT t) = :tagCount")
    Page<Question> findByAllTags(@Param("tags") List<String> tags, @Param("tagCount") Long tagCount, Pageable pageable);
    
    // Find questions used in specific quiz
    @Query("SELECT q FROM Question q JOIN Quiz qz WHERE qz.id = :quizId AND q MEMBER OF qz.questions")
    List<Question> findByQuizId(@Param("quizId") Long quizId);
    
    // Find questions by difficulty level (assuming we add difficulty to Question entity)
    Page<Question> findByDifficultyLevel(String difficultyLevel, Pageable pageable);
    
    // Find questions not used in any quiz
    @Query("SELECT q FROM Question q WHERE q NOT IN (SELECT qz.questions FROM Quiz qz JOIN qz.questions)")
    Page<Question> findUnusedQuestions(Pageable pageable);


    // Find all questions by reference resource id
    @Query("SELECT q FROM Question q WHERE q.referencedResource.id = :resourceId")
    List<Question> findAllByReferencedResourceId(@Param("resourceId") Long resourceId);

}