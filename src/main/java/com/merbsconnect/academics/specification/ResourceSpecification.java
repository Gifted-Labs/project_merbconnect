package com.merbsconnect.academics.specification;

import com.merbsconnect.academics.domain.Resource;
import com.merbsconnect.academics.dto.request.ResourceFilterRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ResourceSpecification {

    /**
     * Creates a specification for filtering resources based on common criteria
     */
    public static <T extends Resource> Specification<T> withFilter(ResourceFilterRequest filter, Class<T> resourceClass) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Handle resource type if specified and we're querying the base Resource class
            if (Resource.class.equals(resourceClass) && StringUtils.hasText(filter.getResourceType())) {
                try {
                    Class<?> specificResourceClass = Class.forName("com.merbsconnect.academics.domain." + filter.getResourceType());
                    predicates.add(criteriaBuilder.equal(root.type(), specificResourceClass));
                } catch (ClassNotFoundException e) {
                    // Log error or handle invalid resource type
                }
            }
            
            // Academic hierarchy filters
            if (filter.getCourseId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("course").get("id"), filter.getCourseId()));
            }
            
            if (filter.getDepartmentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("course").get("department").get("id"), filter.getDepartmentId()));
            }
            
            if (filter.getFacultyId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("course").get("department").get("faculty").get("id"), filter.getFacultyId()));
            }
            
            if (filter.getCollegeId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("course").get("department").get("faculty").get("college").get("id"), filter.getCollegeId()));
            }
            
            if (filter.getProgramId() != null) {
                // Handle program filtering with a subquery
                Subquery<Long> programSubquery = query.subquery(Long.class);
                var programRoot = programSubquery.from(resourceClass);
                Join<Object, Object> courseJoin = programRoot.join("course");
                Join<Object, Object> programsJoin = courseJoin.join("programs");
                
                programSubquery.select(programRoot.get("id"))
                    .where(criteriaBuilder.equal(programsJoin.get("id"), filter.getProgramId()));
                
                predicates.add(criteriaBuilder.in(root.get("id")).value(programSubquery));
            }
            
            if (filter.getSemester() != null) {
                predicates.add(criteriaBuilder.equal(root.get("course").get("semester"), filter.getSemester()));
            }
            
            // Date range filters
            if (filter.getCreatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAfter()));
            }
            
            if (filter.getCreatedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedBefore()));
            }
            
            if (filter.getUpdatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedAfter()));
            }
            
            if (filter.getUpdatedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedBefore()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Overloaded method for backward compatibility
     */
    public static Specification<Resource> withFilter(ResourceFilterRequest filter) {
        return withFilter(filter, Resource.class);
    }
}
