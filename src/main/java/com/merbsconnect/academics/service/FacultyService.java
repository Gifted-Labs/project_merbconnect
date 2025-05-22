package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.request.CreateFacultyRequest;
import com.merbsconnect.academics.dto.request.UpdateFacultyRequest;
import com.merbsconnect.academics.dto.response.FacultyResponse;
import com.merbsconnect.academics.dto.response.PageResponse;

import java.util.List;

/**
 * Service interface for managing Faculty entities.
 */
public interface FacultyService {
    
    /**
     * Creates a new faculty.
     *
     * @param request the faculty creation request
     * @return the created faculty response
     */
    FacultyResponse createFaculty(CreateFacultyRequest request);
    
    /**
     * Updates an existing faculty.
     *
     * @param id the faculty ID
     * @param request the faculty update request
     * @return the updated faculty response
     */
    FacultyResponse updateFaculty(Long id, UpdateFacultyRequest request);
    
    /**
     * Retrieves a faculty by its ID.
     *
     * @param id the faculty ID
     * @return the faculty response
     */
    FacultyResponse getFacultyById(Long id);
    
    /**
     * Retrieves all faculties.
     *
     * @return list of all faculty responses
     */
    List<FacultyResponse> getAllFaculties();
    
    /**
     * Retrieves all faculties with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of faculty responses
     */
    PageResponse<FacultyResponse> getAllFaculties(int page, int size);
    
    /**
     * Retrieves all faculties by college ID.
     *
     * @param collegeId the college ID
     * @return list of faculty responses for the specified college
     */
    List<FacultyResponse> getFacultiesByCollegeId(Long collegeId);
    
    /**
     * Retrieves a faculty by name and college ID.
     *
     * @param facultyName the faculty name
     * @param collegeId the college ID
     * @return the faculty response
     */
    FacultyResponse getFacultyByNameAndCollegeId(String facultyName, Long collegeId);
    
    /**
     * Searches for faculties by name.
     *
     * @param name the name to search for
     * @return list of matching faculty responses
     */
    List<FacultyResponse> searchFacultiesByName(String name);
    
    /**
     * Retrieves a faculty with its departments by ID.
     *
     * @param id the faculty ID
     * @return the faculty response with department information
     */
    FacultyResponse getFacultyWithDepartmentsById(Long id);
    
    /**
     * Retrieves all faculties with their departments by college ID.
     *
     * @param collegeId the college ID
     * @return list of faculty responses with department information
     */
    List<FacultyResponse> getFacultiesWithDepartmentsByCollegeId(Long collegeId);
    
    /**
     * Deletes a faculty by its ID.
     *
     * @param id the faculty ID
     */
    void deleteFaculty(Long id);
    
    /**
     * Checks if a faculty exists by name and college ID.
     *
     * @param facultyName the faculty name
     * @param collegeId the college ID
     * @return true if the faculty exists, false otherwise
     */
    boolean existsByNameAndCollegeId(String facultyName, Long collegeId);
}