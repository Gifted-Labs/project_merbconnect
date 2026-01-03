package com.merbsconnect.academics.controller;

import com.merbsconnect.academics.dto.request.CreateProgramRequest;
import com.merbsconnect.academics.dto.request.UpdateProgramRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ProgramResponse;
import com.merbsconnect.academics.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Program entities.
 */
@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
@Slf4j
public class ProgramController {

    private final ProgramService programService;

    /**
     * Creates a new program.
     *
     * @param request the program creation request
     * @return the created program response wrapped in ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponse>> createProgram(@Valid @RequestBody CreateProgramRequest request) {
        log.info("REST request to create Program: {}", request);
        ProgramResponse response = programService.createProgram(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Program created successfully", response));
    }

    /**
     * Updates an existing program.
     *
     * @param id the program ID
     * @param request the program update request
     * @return the updated program response wrapped in ApiResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponse>> updateProgram(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateProgramRequest request) {
        log.info("REST request to update Program with ID: {}", id);
        ProgramResponse response = programService.updateProgram(id, request);
        return ResponseEntity.ok(ApiResponse.success("Program updated successfully", response));
    }

    /**
     * Retrieves a program by its ID.
     *
     * @param id the program ID
     * @return the program response wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProgramResponse>> getProgramById(@PathVariable Long id) {
        log.info("REST request to get Program with ID: {}", id);
        ProgramResponse response = programService.getProgramById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves a program by its code.
     *
     * @param code the program code
     * @return the program response wrapped in ApiResponse
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ProgramResponse>> getProgramByCode(@PathVariable String code) {
        log.info("REST request to get Program with code: {}", code);
        ProgramResponse response = programService.getProgramByCode(code);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all programs.
     *
     * @return list of all program responses wrapped in ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProgramResponse>>> getAllPrograms() {
        log.info("REST request to get all Programs");
        List<ProgramResponse> responses = programService.getAllPrograms();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves all programs with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of program responses wrapped in ApiResponse
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<ProgramResponse>>> getAllProgramsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all Programs with pagination - page: {}, size: {}", page, size);
        PageResponse<ProgramResponse> response = programService.getAllPrograms(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all programs by department ID.
     *
     * @param departmentId the department ID
     * @return list of program responses for the specified department wrapped in ApiResponse
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<ProgramResponse>>> getProgramsByDepartmentId(@PathVariable Long departmentId) {
        log.info("REST request to get Programs by department ID: {}", departmentId);
        List<ProgramResponse> responses = programService.getProgramsByDepartmentId(departmentId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves all programs by faculty ID.
     *
     * @param facultyId the faculty ID
     * @return list of program responses for the specified faculty wrapped in ApiResponse
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<List<ProgramResponse>>> getProgramsByFacultyId(@PathVariable Long facultyId) {
        log.info("REST request to get Programs by faculty ID: {}", facultyId);
        List<ProgramResponse> responses = programService.getProgramsByFacultyId(facultyId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves all programs by college ID.
     *
     * @param collegeId the college ID
     * @return list of program responses for the specified college wrapped in ApiResponse
     */
    @GetMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<List<ProgramResponse>>> getProgramsByCollegeId(@PathVariable Long collegeId) {
        log.info("REST request to get Programs by college ID: {}", collegeId);
        List<ProgramResponse> responses = programService.getProgramsByCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Searches for programs by name.
     *
     * @param name the name to search for
     * @return list of matching program responses wrapped in ApiResponse
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProgramResponse>>> searchProgramsByName(@RequestParam String name) {
        log.info("REST request to search Programs by name: {}", name);
        List<ProgramResponse> responses = programService.searchProgramsByName(name);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Deletes a program by its ID.
     *
     * @param id the program ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProgram(@PathVariable Long id) {
        log.info("REST request to delete Program with ID: {}", id);
        programService.deleteProgram(id);
        return ResponseEntity.ok(ApiResponse.success("Program deleted successfully", null));
    }

    /**
     * Adds a course to a program.
     *
     * @param programId the program ID
     * @param courseId the course ID
     * @return the updated program response wrapped in ApiResponse
     */
    @PostMapping("/{programId}/courses/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponse>> addCourseToProgram(
            @PathVariable Long programId, 
            @PathVariable Long courseId) {
        log.info("REST request to add Course with ID: {} to Program with ID: {}", courseId, programId);
        ProgramResponse response = programService.addCourseToProgram(programId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Course added to program successfully", response));
    }

    /**
     * Removes a course from a program.
     *
     * @param programId the program ID
     * @param courseId the course ID
     * @return the updated program response wrapped in ApiResponse
     */
    @DeleteMapping("/{programId}/courses/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponse>> removeCourseFromProgram(
            @PathVariable Long programId, 
            @PathVariable Long courseId) {
        log.info("REST request to remove Course with ID: {} from Program with ID: {}", courseId, programId);
        ProgramResponse response = programService.removeCourseFromProgram(programId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Course removed from program successfully", response));
    }
}