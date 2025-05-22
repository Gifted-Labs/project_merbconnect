package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.request.CreateProgramRequest;
import com.merbsconnect.academics.dto.request.UpdateProgramRequest;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ProgramResponse;

import java.util.List;

/**
 * Service interface for managing Program entities.
 */
public interface ProgramService {
    
    /**
     * Creates a new program.
     *
     * @param request the program creation request
     * @return the created program response
     */
    ProgramResponse createProgram(CreateProgramRequest request);
    
    /**
     * Updates an existing program.
     *
     * @param id the program ID
     * @param request the program update request
     * @return the updated program response
     */
    ProgramResponse updateProgram(Long id, UpdateProgramRequest request);
    
    /**
     * Retrieves a program by its ID.
     *
     * @param id the program ID
     * @return the program response
     */
    ProgramResponse getProgramById(Long id);
    
    /**
     * Retrieves a program by its code.
     *
     * @param programCode the program code
     * @return the program response
     */
    ProgramResponse getProgramByCode(String programCode);
    
    /**
     * Retrieves all programs.
     *
     * @return list of all program responses
     */
    List<ProgramResponse> getAllPrograms();
    
    /**
     * Retrieves all programs with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of program responses
     */
    PageResponse<ProgramResponse> getAllPrograms(int page, int size);
    
    /**
     * Retrieves all programs by department ID.
     *
     * @param departmentId the department ID
     * @return list of program responses for the specified department
     */
    List<ProgramResponse> getProgramsByDepartmentId(Long departmentId);
    
    /**
     * Retrieves all programs by faculty ID.
     *
     * @param facultyId the faculty ID
     * @return list of program responses for the specified faculty
     */
    List<ProgramResponse> getProgramsByFacultyId(Long facultyId);
    
    /**
     * Retrieves all programs by college ID.
     *
     * @param collegeId the college ID
     * @return list of program responses for the specified college
     */
    List<ProgramResponse> getProgramsByCollegeId(Long collegeId);
    
    /**
     * Retrieves a program by name and department ID.
     *
     * @param programName the program name
     * @param departmentId the department ID
     * @return the program response
     */
    ProgramResponse getProgramByNameAndDepartmentId(String programName, Long departmentId);
    
    /**
     * Searches for programs by name.
     *
     * @param name the name to search for
     * @return list of matching program responses
     */
    List<ProgramResponse> searchProgramsByName(String name);
    
    /**
     * Deletes a program by its ID.
     *
     * @param id the program ID
     */
    void deleteProgram(Long id);
    
    /**
     * Checks if a program exists by name and department ID.
     *
     * @param programName the program name
     * @param departmentId the department ID
     * @return true if the program exists, false otherwise
     */
    boolean existsByNameAndDepartmentId(String programName, Long departmentId);
    
    /**
     * Checks if a program exists by code.
     *
     * @param programCode the program code
     * @return true if the program exists, false otherwise
     */
    boolean existsByCode(String programCode);
    
    /**
     * Adds a course to a program.
     *
     * @param programId the program ID
     * @param courseId the course ID
     * @return the updated program response
     */
    ProgramResponse addCourseToProgram(Long programId, Long courseId);
    
    /**
     * Removes a course from a program.
     *
     * @param programId the program ID
     * @param courseId the course ID
     * @return the updated program response
     */
    ProgramResponse removeCourseFromProgram(Long programId, Long courseId);
}