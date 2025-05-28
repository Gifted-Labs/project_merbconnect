package com.merbsconnect.academics.controller;

import com.merbsconnect.academics.dto.request.CreateReferenceMaterialRequest;
import com.merbsconnect.academics.dto.request.UpdateReferenceMaterialRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ReferenceMaterialResponse;
import com.merbsconnect.academics.service.ReferenceMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing ReferenceMaterial entities.
 */
@RestController
@RequestMapping("/api/v1/reference-materials")
@RequiredArgsConstructor
@Slf4j
public class ReferenceMaterialController {

    private final ReferenceMaterialService referenceMaterialService;

    /**
     * Creates a new reference material.
     *
     * @param request the reference material creation request
     * @return the created reference material response wrapped in ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReferenceMaterialResponse>> createReferenceMaterial(
            @Valid @RequestBody CreateReferenceMaterialRequest request) {
        log.info("REST request to create Reference Material: {}", request);
        ApiResponse<ReferenceMaterialResponse> response = referenceMaterialService.createReferenceMaterial(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reference Material created successfully", response.getData()));
    }

    /**
     * Updates an existing reference material.
     *
     * @param id the reference material ID
     * @param request the reference material update request
     * @return the updated reference material response wrapped in ApiResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReferenceMaterialResponse>> updateReferenceMaterial(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReferenceMaterialRequest request) {
        log.info("REST request to update Reference Material with ID: {}", id);
        ApiResponse<ReferenceMaterialResponse> response = referenceMaterialService.updateReferenceMaterial(id, request);
        return ResponseEntity.ok(ApiResponse.success("Reference Material updated successfully", response.getData()));
    }

    /**
     * Retrieves a reference material by its ID.
     *
     * @param id the reference material ID
     * @return the reference material response wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReferenceMaterialResponse>> getReferenceMaterialById(@PathVariable Long id) {
        log.info("REST request to get Reference Material with ID: {}", id);
        ApiResponse<ReferenceMaterialResponse> response = referenceMaterialService.getReferenceMaterialById(id);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves all reference materials with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of reference material responses wrapped in ApiResponse
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<ReferenceMaterialResponse>>> getAllReferenceMaterialsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all Reference Materials with pagination - page: {}, size: {}", page, size);
        ApiResponse<PageResponse<ReferenceMaterialResponse>> response = referenceMaterialService.getAllReferenceMaterials(page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves reference materials by format with pagination.
     *
     * @param format the reference material format
     * @param page the page number
     * @param size the page size
     * @return paginated list of reference material responses wrapped in ApiResponse
     */
    @GetMapping("/format/{format}")
    public ResponseEntity<ApiResponse<PageResponse<ReferenceMaterialResponse>>> getReferenceMaterialsByFormat(
            @PathVariable String format,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Reference Materials by format: {} with pagination - page: {}, size: {}", 
                format, page, size);
        ApiResponse<PageResponse<ReferenceMaterialResponse>> response = 
                referenceMaterialService.getReferenceMaterialsByFormat(format, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves reference materials by course ID and format with pagination.
     *
     * @param courseId the course ID
     * @param format the reference material format (optional)
     * @param page the page number
     * @param size the page size
     * @return paginated list of reference material responses wrapped in ApiResponse
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<PageResponse<ReferenceMaterialResponse>>> getReferenceMaterialsByCourseIdAndFormat(
            @PathVariable Long courseId,
            @RequestParam(required = false) String format,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Reference Materials by course ID: {} and format: {} with pagination - page: {}, size: {}", 
                courseId, format, page, size);
        ApiResponse<PageResponse<ReferenceMaterialResponse>> response = 
                referenceMaterialService.getReferenceMaterialsByCourseIdAndFormat(courseId, format, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves reference materials by department ID and format with pagination.
     *
     * @param departmentId the department ID
     * @param format the reference material format (optional)
     * @param page the page number
     * @param size the page size
     * @return paginated list of reference material responses wrapped in ApiResponse
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<PageResponse<ReferenceMaterialResponse>>> getReferenceMaterialsByDepartmentIdAndFormat(
            @PathVariable Long departmentId,
            @RequestParam(required = false) String format,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Reference Materials by department ID: {} and format: {} with pagination - page: {}, size: {}", 
                departmentId, format, page, size);
        ApiResponse<PageResponse<ReferenceMaterialResponse>> response = 
                referenceMaterialService.getReferenceMaterialsByDepartmentIdAndFormat(departmentId, format, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves reference materials by language with pagination.
     *
     * @param language the reference material language
     * @param page the page number
     * @param size the page size
     * @return paginated list of reference material responses wrapped in ApiResponse
     */
    @GetMapping("/language/{language}")
    public ResponseEntity<ApiResponse<PageResponse<ReferenceMaterialResponse>>> getReferenceMaterialsByLanguage(
            @PathVariable String language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Reference Materials by language: {} with pagination - page: {}, size: {}", 
                language, page, size);
        ApiResponse<PageResponse<ReferenceMaterialResponse>> response = 
                referenceMaterialService.getReferenceMaterialsByLanguage(language, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Searches reference materials by title with pagination.
     *
     * @param keyword the search keyword
     * @param page the page number
     * @param size the page size
     * @return paginated list of reference material responses wrapped in ApiResponse
     */
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<PageResponse<ReferenceMaterialResponse>>> searchReferenceMaterialsByTitle(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search Reference Materials by title containing: {} with pagination - page: {}, size: {}", 
                keyword, page, size);
        ApiResponse<PageResponse<ReferenceMaterialResponse>> response = 
                referenceMaterialService.searchReferenceMaterialsByTitle(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Searches reference materials by author with pagination.
     *
     * @param author the author name
     * @param page the page number
     * @param size the page size
     * @return paginated list of reference material responses wrapped in ApiResponse
     */
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<PageResponse<ReferenceMaterialResponse>>> searchReferenceMaterialsByAuthor(
            @RequestParam String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search Reference Materials by author containing: {} with pagination - page: {}, size: {}", 
                author, page, size);
        ApiResponse<PageResponse<ReferenceMaterialResponse>> response = 
                referenceMaterialService.searchReferenceMaterialsByAuthor(author, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves distinct formats of reference materials.
     *
     * @return list of distinct formats wrapped in ApiResponse
     */
    @GetMapping("/formats")
    public ResponseEntity<ApiResponse<List<String>>> getDistinctFormats() {
        log.info("REST request to get distinct formats of Reference Materials");
        ApiResponse<List<String>> response = referenceMaterialService.getDistinctFormats();
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves distinct languages of reference materials.
     *
     * @return list of distinct languages wrapped in ApiResponse
     */
    @GetMapping("/languages")
    public ResponseEntity<ApiResponse<List<String>>> getDistinctLanguages() {
        log.info("REST request to get distinct languages of Reference Materials");
        ApiResponse<List<String>> response = referenceMaterialService.getDistinctLanguages();
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Deletes a reference material by its ID.
     *
     * @param id the reference material ID
     * @return success message wrapped in ApiResponse
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReferenceMaterial(@PathVariable Long id) {
        log.info("REST request to delete Reference Material with ID: {}", id);
        // Assuming there's a delete method in the service
        referenceMaterialService.deleteReferenceMaterial(id);
        return ResponseEntity.ok(ApiResponse.success("Reference Material deleted successfully", null));
    }
}