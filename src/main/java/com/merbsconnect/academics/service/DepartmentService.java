package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.request.CreateDepartmentRequest;
import com.merbsconnect.academics.dto.request.UpdateDepartmentRequest;
import com.merbsconnect.academics.dto.response.DepartmentResponse;
import com.merbsconnect.academics.dto.response.PageResponse;

import java.util.List;

/**
 * Service interface for managing Department entities.
 */
public interface DepartmentService {
    
    /**
     * Creates a new department.
     *
     * @param request the department creation request
     * @return the created department response
     */
    DepartmentResponse createDepartment(CreateDepartmentRequest request);
    
    /**
     * Updates an existing department.
     *
     * @param id the department ID
     * @param request the department update request
     * @return the updated department response
     */
    DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request);
    
    /**
     * Retrieves a department by its ID.
     *
     * @param id the department ID
     * @return the department response
     */
    DepartmentResponse getDepartmentById(Long id);
    
    /**
     * Retrieves all departments.
     *
     * @return list of all department responses
     */
    List<DepartmentResponse> getAllDepartments();
    
    /**
     * Retrieves all departments with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of department responses
     */
    PageResponse<DepartmentResponse> getAllDepartments(int page, int size);
    
    /**
     * Retrieves all departments by faculty ID.
     *
     * @param facultyId the faculty ID
     * @return list of department responses for the specified faculty
     */
    List<DepartmentResponse> getDepartmentsByFacultyId(Long facultyId);
    
    /**
     * Retrieves all departments by college ID.
     *
     * @param collegeId the college ID
     * @return list of department responses for the specified college
     */
    List<DepartmentResponse> getDepartmentsByCollegeId(Long collegeId);
    
    /**
     * Retrieves a department by name and faculty ID.
     *
     * @param departmentName the department name
     * @param facultyId the faculty ID
     * @return the department response
     */
    DepartmentResponse getDepartmentByNameAndFacultyId(String departmentName, Long facultyId);
    
    /**
     * Searches for departments by name.
     *
     * @param name the name to search for
     * @return list of matching department responses
     */
    List<DepartmentResponse> searchDepartmentsByName(String name);
    
    /**
     * Retrieves a department with its programs by ID.
     *
     * @param id the department ID
     * @return the department response with program information
     */
    DepartmentResponse getDepartmentWithProgramsById(Long id);
    
    /**
     * Retrieves all departments with their programs by faculty ID.
     *
     * @param facultyId the faculty ID
     * @return list of department responses with program information
     */
    List<DepartmentResponse> getDepartmentsWithProgramsByFacultyId(Long facultyId);
    
    /**
     * Deletes a department by its ID.
     *
     * @param id the department ID
     */
    void deleteDepartment(Long id);
    
    /**
     * Checks if a department exists by name and faculty ID.
     *
     * @param departmentName the department name
     * @param facultyId the faculty ID
     * @return true if the department exists, false otherwise
     */
    boolean existsByNameAndFacultyId(String departmentName, Long facultyId);
}