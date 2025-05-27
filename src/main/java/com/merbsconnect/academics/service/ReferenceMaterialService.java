package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.request.CreateReferenceMaterialRequest;
import com.merbsconnect.academics.dto.request.UpdateReferenceMaterialRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ReferenceMaterialResponse;

import java.util.List;

/**
 * Service interface for managing ReferenceMaterial entities.
 */
public interface ReferenceMaterialService {
    
    /**
     * Creates a new reference material.
     *
     * @param request the reference material creation request
     * @return API response containing the created reference material
     */
    ApiResponse<ReferenceMaterialResponse> createReferenceMaterial(CreateReferenceMaterialRequest request);
    
    /**
     * Updates an existing reference material.
     *
     * @param id the reference material ID
     * @param request the reference material update request
     * @return API response containing the updated reference material
     */
    ApiResponse<ReferenceMaterialResponse> updateReferenceMaterial(Long id, UpdateReferenceMaterialRequest request);
    
    /**
     * Retrieves a reference material by its ID.
     *
     * @param id the reference material ID
     * @return API response containing the reference material
     */
    ApiResponse<ReferenceMaterialResponse> getReferenceMaterialById(Long id);
    
    /**
     * Deletes a reference material by its ID.
     *
     * @param id the reference material ID
     * @return API response indicating success or failure
     */
    ApiResponse<Void> deleteReferenceMaterial(Long id);
    
    /**
     * Retrieves all reference materials with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated reference materials
     */
    ApiResponse<PageResponse<ReferenceMaterialResponse>> getAllReferenceMaterials(int page, int size);
    
    /**
     * Retrieves reference materials by format with pagination.
     *
     * @param format the format
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated reference materials
     */
    ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByFormat(
            String format, int page, int size);
    
    /**
     * Retrieves reference materials by course ID and format with pagination.
     *
     * @param courseId the course ID
     * @param format the format (can be null)
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated reference materials
     */
    ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByCourseIdAndFormat(
            Long courseId, String format, int page, int size);
    
    /**
     * Retrieves reference materials by department ID and format with pagination.
     *
     * @param departmentId the department ID
     * @param format the format (can be null)
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated reference materials
     */
    ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByDepartmentIdAndFormat(
            Long departmentId, String format, int page, int size);
    
    /**
     * Retrieves reference materials by language with pagination.
     *
     * @param language the language
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated reference materials
     */
    ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByLanguage(
            String language, int page, int size);
    
    /**
     * Retrieves reference materials by publication year with pagination.
     *
     * @param publicationYear the publication year
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated reference materials
     */
    ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByPublicationYear(
            String publicationYear, int page, int size);
    
    /**
     * Searches reference materials by title with pagination.
     *
     * @param keyword the search keyword
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated reference materials
     */
    ApiResponse<PageResponse<ReferenceMaterialResponse>> searchReferenceMaterialsByTitle(
            String keyword, int page, int size);
    
    /**
     * Searches reference materials by author with pagination.
     *
     * @param author the author name
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated reference materials
     */
    ApiResponse<PageResponse<ReferenceMaterialResponse>> searchReferenceMaterialsByAuthor(
            String author, int page, int size);
    
    /**
     * Retrieves all distinct formats.
     *
     * @return API response containing list of distinct formats
     */
    ApiResponse<List<String>> getDistinctFormats();
    
    /**
     * Retrieves all distinct languages.
     *
     * @return API response containing list of distinct languages
     */
    ApiResponse<List<String>> getDistinctLanguages();
    
    /**
     * Retrieves all distinct publication years.
     *
     * @return API response containing list of distinct publication years
     */
    ApiResponse<List<String>> getDistinctPublicationYears();
}
