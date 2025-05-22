package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.request.CreateCollegeRequest;
import com.merbsconnect.academics.dto.request.UpdateCollegeRequest;
import com.merbsconnect.academics.dto.response.CollegeResponse;
import com.merbsconnect.academics.dto.response.PageResponse;

import java.util.List;

/**
 * Service interface for managing College entities.
 */
public interface CollegeService {
    
    /**
     * Creates a new college.
     *
     * @param request the college creation request
     * @return the created college response
     */
    CollegeResponse createCollege(CreateCollegeRequest request);
    
    /**
     * Updates an existing college.
     *
     * @param id the college ID
     * @param request the college update request
     * @return the updated college response
     */
    CollegeResponse updateCollege(Long id, UpdateCollegeRequest request);
    
    /**
     * Retrieves a college by its ID.
     *
     * @param id the college ID
     * @return the college response
     */
    CollegeResponse getCollegeById(Long id);
    
    /**
     * Retrieves a college by its name.
     *
     * @param collegeName the college name
     * @return the college response
     */
    CollegeResponse getCollegeByName(String collegeName);
    
    /**
     * Retrieves all colleges.
     *
     * @return list of all college responses
     */
    List<CollegeResponse> getAllColleges();
    
    /**
     * Retrieves all colleges with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of college responses
     */
    PageResponse<CollegeResponse> getAllColleges(int page, int size);
    
    /**
     * Searches for colleges by name.
     *
     * @param name the name to search for
     * @return list of matching college responses
     */
    List<CollegeResponse> searchCollegesByName(String name);
    
    /**
     * Retrieves all colleges with their faculties.
     *
     * @return list of college responses with faculty information
     */
    List<CollegeResponse> getAllCollegesWithFaculties();
    
    /**
     * Retrieves a college with its faculties by ID.
     *
     * @param id the college ID
     * @return the college response with faculty information
     */
    CollegeResponse getCollegeWithFacultiesById(Long id);
    
    /**
     * Deletes a college by its ID.
     *
     * @param id the college ID
     */
    void deleteCollege(Long id);
    
    /**
     * Checks if a college exists by name.
     *
     * @param collegeName the college name
     * @return true if the college exists, false otherwise
     */
    boolean existsByName(String collegeName);
}
