package com.merbsconnect.academics.specification;

import com.merbsconnect.academics.domain.Quiz;
import com.merbsconnect.academics.dto.request.ResourceFilterRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class QuizSpecification {

    public static Specification<Quiz> withFilter(ResourceFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Apply base Resource filters
            Specification<Quiz> baseSpec = ResourceSpecification.withFilter(filter, Quiz.class);
            predicates.add(baseSpec.toPredicate(root, query, criteriaBuilder));
            
            // Quiz-specific filters
            if (StringUtils.hasText(filter.getDifficultyLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("difficultyLevel"), filter.getDifficultyLevel()));
            }
            
            if (filter.getQuizType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("quizType"), filter.getQuizType()));
            }
            
            // Year range filters
            addYearRangePredicates(filter, root, criteriaBuilder, predicates);
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Helper method to add year range predicates
     */
    private static void addYearRangePredicates(ResourceFilterRequest filter, 
                                              jakarta.persistence.criteria.Root<Quiz> root, 
                                              jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                              List<Predicate> predicates) {
        if (filter.getYearFrom() != null && filter.getYearTo() != null) {
            predicates.add(criteriaBuilder.between(root.get("yearGiven"), filter.getYearFrom(), filter.getYearTo()));
        } else if (filter.getYearFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("yearGiven"), filter.getYearFrom()));
        } else if (filter.getYearTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("yearGiven"), filter.getYearTo()));
        }
    }
}