package com.merbsconnect.academics.specification;

import com.merbsconnect.academics.domain.Question;
import com.merbsconnect.academics.dto.request.QuestionSearchRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;

public class QuestionSpecification {

    /**
     * Creates a specification for filtering questions based on search criteria
     */
    public static Specification<Question> withSearchCriteria(QuestionSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by keyword (question text)
            if (StringUtils.hasText(request.getKeyword())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("questionText")), 
                    "%" + request.getKeyword().toLowerCase() + "%"
                ));
            }
            
            // Filter by difficulty level
            if (StringUtils.hasText(request.getDifficultyLevel())) {
                predicates.add(criteriaBuilder.equal(
                    root.get("difficultyLevel"), 
                    request.getDifficultyLevel()
                ));
            }
            
            // Filter by referenced resource
            if (request.getReferencedResourceId() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("referencedResource").get("id"), 
                    request.getReferencedResourceId()
                ));
            }
            
            // Filter by tags
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                // For each tag, create a predicate that checks if the tag is in the question's tags list
                for (String tag : request.getTags()) {
                    // Using MEMBER OF for collections
                    predicates.add(criteriaBuilder.isMember(tag, root.get("tags")));
                }
            }
            
            // Filter unused questions (not associated with any quiz)
            if (Boolean.TRUE.equals(request.getUnusedOnly())) {
                predicates.add(createUnusedQuestionsPredicate(root, query, criteriaBuilder));
            }
            
            // Filter by course ID
            if (request.getCourseId() != null) {
                predicates.add(createCourseFilterPredicate(root, query, criteriaBuilder, request.getCourseId()));
            }
            
            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Creates a predicate to filter questions that are not associated with any quiz
     */
    private static Predicate createUnusedQuestionsPredicate(
            jakarta.persistence.criteria.Root<Question> root,
            jakarta.persistence.criteria.CriteriaQuery<?> query,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
        
        // Create a subquery to find questions that are used in quizzes
        Subquery<Long> subquery = query.subquery(Long.class);
        var quizRoot = subquery.from(com.merbsconnect.academics.domain.Quiz.class);
        Join<Object, Object> questionsJoin = quizRoot.join("questions");
        
        subquery.select(questionsJoin.get("id"));
        
        // Return a predicate that checks if the question ID is NOT IN the subquery result
        return criteriaBuilder.not(root.get("id").in(subquery));
    }
    
    /**
     * Creates a predicate to filter questions by course ID
     * This will find questions that either:
     * 1. Reference a resource that belongs to the specified course, or
     * 2. Belong to a quiz that is associated with the specified course
     */
    private static Predicate createCourseFilterPredicate(
            jakarta.persistence.criteria.Root<Question> root,
            jakarta.persistence.criteria.CriteriaQuery<?> query,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            Long courseId) {
        
        List<Predicate> coursePredicates = new ArrayList<>();
        
        // 1. Questions that reference a resource from the specified course
        coursePredicates.add(criteriaBuilder.equal(
            root.get("referencedResource").get("course").get("id"), 
            courseId
        ));
        
        // 2. Questions that belong to quizzes from the specified course
        Subquery<Long> quizSubquery = query.subquery(Long.class);
        var quizRoot = quizSubquery.from(com.merbsconnect.academics.domain.Quiz.class);
        Join<Object, Object> questionsJoin = quizRoot.join("questions");
        
        quizSubquery.select(questionsJoin.get("id"))
            .where(criteriaBuilder.equal(quizRoot.get("course").get("id"), courseId));
        
        coursePredicates.add(root.get("id").in(quizSubquery));
        
        // Combine with OR since a question can match either condition
        return criteriaBuilder.or(coursePredicates.toArray(new Predicate[0]));
    }
    
    /**
     * Creates a specification to find questions by tag
     */
    public static Specification<Question> withTag(String tag) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isMember(tag, root.get("tags"));
    }
    
    /**
     * Creates a specification to find questions by multiple tags (AND logic)
     * This will return questions that have ALL the specified tags
     */
    public static Specification<Question> withAllTags(List<String> tags) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            for (String tag : tags) {
                predicates.add(criteriaBuilder.isMember(tag, root.get("tags")));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Creates a specification to find questions by multiple tags (OR logic)
     * This will return questions that have ANY of the specified tags
     */
    public static Specification<Question> withAnyTag(List<String> tags) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            for (String tag : tags) {
                predicates.add(criteriaBuilder.isMember(tag, root.get("tags")));
            }
            
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Creates a specification to find questions by text content
     */
    public static Specification<Question> withTextContent(String keyword) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("questionText")),
                "%" + keyword.toLowerCase() + "%"
            );
    }
}