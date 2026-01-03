package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ResourceSummaryResponse;

/**
 * Service interface for managing Resource entities.
 * This is a base service for all resource types.
 */
public interface ResourceService {
    
    /**
     * Retrieves a resource by its ID.
     *
     * @param id the resource ID
     * @return API response containing the resource summary
     */
    ApiResponse<ResourceSummaryResponse> getResourceById(Long id);
    
    /**
     * Deletes a resource by its ID.
     *
     * @param id the resource ID
     * @return API response indicating success or failure
     */
    ApiResponse<Void> deleteResource(Long id);
    
    /**
     * Retrieves all resources with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated resources
     */
    ApiResponse<PageResponse<ResourceSummaryResponse>> getAllResources(int page, int size);
    
    /**
     * Retrieves resources by course ID with pagination.
     *
     * @param courseId the course ID
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated resources
     */
    ApiResponse<PageResponse<ResourceSummaryResponse>> getResourcesByCourseId(Long courseId, int page, int size);
    
    /**
     * Retrieves resources by department ID with pagination.
     *
     * @param departmentId the department ID
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated resources
     */
    ApiResponse<PageResponse<ResourceSummaryResponse>> getResourcesByDepartmentId(Long departmentId, int page, int size);
    
    /**
     * Retrieves resources by faculty ID with pagination.
     *
     * @param facultyId the faculty ID
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated resources
     */
    ApiResponse<PageResponse<ResourceSummaryResponse>> getResourcesByFacultyId(Long facultyId, int page, int size);
    
    /**
     * Retrieves resources by college ID with pagination.
     *
     * @param collegeId the college ID
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated resources
     */
    ApiResponse<PageResponse<ResourceSummaryResponse>> getResourcesByCollegeId(Long collegeId, int page, int size);
    
    /**
     * Searches resources by title with pagination.
     *
     * @param keyword the search keyword
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated resources
     */
    ApiResponse<PageResponse<ResourceSummaryResponse>> searchResourcesByTitle(String keyword, int page, int size);
    
    /**
     * Searches resources by title and course ID with pagination.
     *
     * @param keyword the search keyword
     * @param courseId the course ID
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated resources
     */
    ApiResponse<PageResponse<ResourceSummaryResponse>> searchResourcesByTitleAndCourseId(
            String keyword, Long courseId, int page, int size);
}
