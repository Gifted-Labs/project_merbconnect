package com.merbsconnect.academics.controller;

import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ResourceSummaryResponse;
import com.merbsconnect.academics.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing academic resources.
 */
@RestController
@RequestMapping("/api/v1/academics/resources")
@RequiredArgsConstructor
@Slf4j
@Component("academicsResourceController")
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * Retrieves a resource by its ID.
     *
     * @param id the resource ID
     * @return the resource response wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceSummaryResponse>> getResourceById(@PathVariable Long id) {
        log.info("REST request to get Resource with ID: {}", id);
        ApiResponse<ResourceSummaryResponse> response = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Deletes a resource by its ID.
     *
     * @param id the resource ID
     * @return success message wrapped in ApiResponse
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        log.info("REST request to delete Resource with ID: {}", id);
        ApiResponse<Void> response = resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully", null));
    }

    /**
     * Retrieves all resources with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of resource responses wrapped in ApiResponse
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<ResourceSummaryResponse>>> getAllResourcesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all Resources with pagination - page: {}, size: {}", page, size);
        ApiResponse<PageResponse<ResourceSummaryResponse>> response = resourceService.getAllResources(page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves resources by course ID with pagination.
     *
     * @param courseId the course ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of resource responses wrapped in ApiResponse
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<PageResponse<ResourceSummaryResponse>>> getResourcesByCourseId(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Resources by course ID: {} with pagination - page: {}, size: {}", courseId, page, size);
        ApiResponse<PageResponse<ResourceSummaryResponse>> response = resourceService.getResourcesByCourseId(courseId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves resources by department ID with pagination.
     *
     * @param departmentId the department ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of resource responses wrapped in ApiResponse
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<PageResponse<ResourceSummaryResponse>>> getResourcesByDepartmentId(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Resources by department ID: {} with pagination - page: {}, size: {}", departmentId, page, size);
        ApiResponse<PageResponse<ResourceSummaryResponse>> response = resourceService.getResourcesByDepartmentId(departmentId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves resources by faculty ID with pagination.
     *
     * @param facultyId the faculty ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of resource responses wrapped in ApiResponse
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<PageResponse<ResourceSummaryResponse>>> getResourcesByFacultyId(
            @PathVariable Long facultyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Resources by faculty ID: {} with pagination - page: {}, size: {}", facultyId, page, size);
        ApiResponse<PageResponse<ResourceSummaryResponse>> response = resourceService.getResourcesByFacultyId(facultyId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves resources by college ID with pagination.
     *
     * @param collegeId the college ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of resource responses wrapped in ApiResponse
     */
    @GetMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<PageResponse<ResourceSummaryResponse>>> getResourcesByCollegeId(
            @PathVariable Long collegeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Resources by college ID: {} with pagination - page: {}, size: {}", collegeId, page, size);
        ApiResponse<PageResponse<ResourceSummaryResponse>> response = resourceService.getResourcesByCollegeId(collegeId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Searches resources by title with pagination.
     *
     * @param keyword the search keyword
     * @param page the page number
     * @param size the page size
     * @return paginated list of matching resource responses wrapped in ApiResponse
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ResourceSummaryResponse>>> searchResourcesByTitle(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search Resources by title: {} with pagination - page: {}, size: {}", keyword, page, size);
        ApiResponse<PageResponse<ResourceSummaryResponse>> response = resourceService.searchResourcesByTitle(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Searches resources by title and course ID with pagination.
     *
     * @param keyword the search keyword
     * @param courseId the course ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of matching resource responses wrapped in ApiResponse
     */
    @GetMapping("/search/course/{courseId}")
    public ResponseEntity<ApiResponse<PageResponse<ResourceSummaryResponse>>> searchResourcesByTitleAndCourseId(
            @RequestParam String keyword,
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search Resources by title: {} and course ID: {} with pagination - page: {}, size: {}", 
                keyword, courseId, page, size);
        ApiResponse<PageResponse<ResourceSummaryResponse>> response = 
                resourceService.searchResourcesByTitleAndCourseId(keyword, courseId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }
}