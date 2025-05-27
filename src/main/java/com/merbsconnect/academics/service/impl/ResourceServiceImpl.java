package com.merbsconnect.academics.service.impl;

import com.merbsconnect.academics.domain.Resource;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ResourceSummaryResponse;
import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.academics.repository.CourseRepository;
import com.merbsconnect.academics.repository.ResourceRepository;
import com.merbsconnect.academics.service.ResourceService;
import com.merbsconnect.academics.util.ResourceMapper;
import com.merbsconnect.util.mapper.PaginationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the ResourceService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ResourceSummaryResponse> getResourceById(Long id) {
        log.info("Retrieving resource with ID: {}", id);
        Resource resource = findResourceById(id);
        return ApiResponse.success(ResourceMapper.toResourceSummaryResponse(resource));
    }

    @Override
    public ApiResponse<Void> deleteResource(Long id) {
        log.info("Deleting resource with ID: {}", id);
        
        // Check if resource exists
        if (!resourceRepository.existsById(id)) {
            log.error("Resource not found with ID: {}", id);
            throw new ResourceNotFoundException("Resource not found with ID: " + id);
        }
        
        resourceRepository.deleteById(id);
        
        log.info("Resource deleted successfully with ID: {}", id);
        return ApiResponse.success("Resource deleted successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ResourceSummaryResponse>> getAllResources(int page, int size) {
        log.info("Retrieving all resources with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Resource> resourcePage = resourceRepository.findAll(pageable);
        
        PageResponse<ResourceSummaryResponse> pageResponse = PaginationMapper.mapToPageResponse(
                resourcePage, ResourceMapper::toResourceSummaryResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ResourceSummaryResponse>> getResourcesByCourseId(Long courseId, int page, int size) {
        log.info("Retrieving resources by course ID: {} with pagination - page: {}, size: {}", courseId, page, size);
        
        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            log.error("Course not found with ID: {}", courseId);
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Resource> resourcePage = resourceRepository.findByCourseId(courseId, pageable);
        
        PageResponse<ResourceSummaryResponse> pageResponse = PaginationMapper.mapToPageResponse(
                resourcePage, ResourceMapper::toResourceSummaryResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ResourceSummaryResponse>> getResourcesByDepartmentId(Long departmentId, int page, int size) {
        log.info("Retrieving resources by department ID: {} with pagination - page: {}, size: {}", departmentId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Resource> resourcePage = resourceRepository.findByDepartmentId(departmentId, pageable);
        
        PageResponse<ResourceSummaryResponse> pageResponse = PaginationMapper.mapToPageResponse(
                resourcePage, ResourceMapper::toResourceSummaryResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ResourceSummaryResponse>> getResourcesByFacultyId(Long facultyId, int page, int size) {
        log.info("Retrieving resources by faculty ID: {} with pagination - page: {}, size: {}", facultyId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Resource> resourcePage = resourceRepository.findByFacultyId(facultyId, pageable);
        
        PageResponse<ResourceSummaryResponse> pageResponse = PaginationMapper.mapToPageResponse(
                resourcePage, ResourceMapper::toResourceSummaryResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ResourceSummaryResponse>> getResourcesByCollegeId(Long collegeId, int page, int size) {
        log.info("Retrieving resources by college ID: {} with pagination - page: {}, size: {}", collegeId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Resource> resourcePage = resourceRepository.findByCollegeId(collegeId, pageable);
        
        PageResponse<ResourceSummaryResponse> pageResponse = PaginationMapper.mapToPageResponse(
                resourcePage, ResourceMapper::toResourceSummaryResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ResourceSummaryResponse>> searchResourcesByTitle(String keyword, int page, int size) {
        log.info("Searching resources by title containing: {} with pagination - page: {}, size: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Using a specification for the search
        Page<Resource> resourcePage = resourceRepository.findAll((root, query, cb) -> 
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"), pageable);
        
        PageResponse<ResourceSummaryResponse> pageResponse = PaginationMapper.mapToPageResponse(
                resourcePage, ResourceMapper::toResourceSummaryResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ResourceSummaryResponse>> searchResourcesByTitleAndCourseId(
            String keyword, Long courseId, int page, int size) {
        log.info("Searching resources by title containing: {} and course ID: {} with pagination - page: {}, size: {}", 
                keyword, courseId, page, size);
        
        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            log.error("Course not found with ID: {}", courseId);
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Resource> resourcePage = resourceRepository.searchByTitleAndCourseId(keyword, courseId, pageable);
        
        PageResponse<ResourceSummaryResponse> pageResponse = PaginationMapper.mapToPageResponse(
                resourcePage, ResourceMapper::toResourceSummaryResponse);
        
        return ApiResponse.success(pageResponse);
    }
    
    /**
     * Helper method to find a resource by ID.
     *
     * @param id the resource ID
     * @return the resource entity
     * @throws ResourceNotFoundException if resource not found
     */
    private Resource findResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Resource not found with ID: {}", id);
                    return new ResourceNotFoundException("Resource not found with ID: " + id);
                });
    }
}
