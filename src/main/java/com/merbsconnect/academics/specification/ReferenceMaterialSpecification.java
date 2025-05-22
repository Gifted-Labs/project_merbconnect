package com.merbsconnect.academics.specification;

import com.merbsconnect.academics.domain.ReferenceMaterial;
import com.merbsconnect.academics.dto.request.ResourceFilterRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ReferenceMaterialSpecification {

    public static Specification<ReferenceMaterial> withFilter(ResourceFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Apply base Resource filters
            Specification<ReferenceMaterial> baseSpec = ResourceSpecification.withFilter(filter, ReferenceMaterial.class);
            predicates.add(baseSpec.toPredicate(root, query, criteriaBuilder));
            
            // Reference material-specific filters
            addAuthorFilter(filter, root, criteriaBuilder, predicates);
            addPublisherFilter(filter, root, criteriaBuilder, predicates);
            addIsbnFilter(filter, root, criteriaBuilder, predicates);
            addFormatFilter(filter, root, criteriaBuilder, predicates);
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Helper method to add author filter
     */
    private static void addAuthorFilter(ResourceFilterRequest filter,
                                       jakarta.persistence.criteria.Root<ReferenceMaterial> root,
                                       jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                       List<Predicate> predicates) {
        if (StringUtils.hasText(filter.getAuthor())) {
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("author")), 
                "%" + filter.getAuthor().toLowerCase() + "%"
            ));
        }
    }
    
    /**
     * Helper method to add publisher filter
     */
    private static void addPublisherFilter(ResourceFilterRequest filter,
                                          jakarta.persistence.criteria.Root<ReferenceMaterial> root,
                                          jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                          List<Predicate> predicates) {
        if (StringUtils.hasText(filter.getPublisher())) {
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("publisher")), 
                "%" + filter.getPublisher().toLowerCase() + "%"
            ));
        }
    }
    
    /**
     * Helper method to add ISBN filter
     */
    private static void addIsbnFilter(ResourceFilterRequest filter,
                                     jakarta.persistence.criteria.Root<ReferenceMaterial> root,
                                     jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                     List<Predicate> predicates) {
        if (StringUtils.hasText(filter.getIsbn())) {
            predicates.add(criteriaBuilder.equal(root.get("isbn"), filter.getIsbn()));
        }
    }
    
    /**
     * Helper method to add format filter
     */
    private static void addFormatFilter(ResourceFilterRequest filter,
                                       jakarta.persistence.criteria.Root<ReferenceMaterial> root,
                                       jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                       List<Predicate> predicates) {
        if (StringUtils.hasText(filter.getFormat())) {
            predicates.add(criteriaBuilder.equal(root.get("format"), filter.getFormat()));
        }
    }
}