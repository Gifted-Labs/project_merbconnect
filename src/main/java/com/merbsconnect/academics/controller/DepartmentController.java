package com.merbsconnect.academics.controller;

import com.merbsconnect.academics.dto.request.CreateDepartmentRequest;
import com.merbsconnect.academics.dto.request.UpdateDepartmentRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.DepartmentResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Department entities.
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Creates a new department.
     *
     * @param request the department creation request
     * @return the created department response wrapped in ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        log.info("REST request to create Department: {}", request);
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created successfully", response));
    }

    /**
     * Updates an existing department.
     *
     * @param id the department ID
     * @param request the department update request
     * @return the updated department response wrapped in ApiResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        log.info("REST request to update Department with ID: {}", id);
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", response));
    }

    /**
     * Retrieves a department by its ID.
     *
     * @param id the department ID
     * @return the department response wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable Long id) {
        log.info("REST request to get Department with ID: {}", id);
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all departments.
     *
     * @return list of all department responses wrapped in ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        log.info("REST request to get all Departments");
        List<DepartmentResponse> responses = departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves all departments with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of department responses wrapped in ApiResponse
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<DepartmentResponse>>> getAllDepartmentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all Departments with pagination - page: {}, size: {}", page, size);
        PageResponse<DepartmentResponse> response = departmentService.getAllDepartments(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all departments by faculty ID.
     *
     * @param facultyId the faculty ID
     * @return list of department responses for the specified faculty wrapped in ApiResponse
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartmentsByFacultyId(@PathVariable Long facultyId) {
        log.info("REST request to get Departments by faculty ID: {}", facultyId);
        List<DepartmentResponse> responses = departmentService.getDepartmentsByFacultyId(facultyId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves all departments by college ID.
     *
     * @param collegeId the college ID
     * @return list of department responses for the specified college wrapped in ApiResponse
     */
    @GetMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartmentsByCollegeId(@PathVariable Long collegeId) {
        log.info("REST request to get Departments by college ID: {}", collegeId);
        List<DepartmentResponse> responses = departmentService.getDepartmentsByCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves a department by name and faculty ID.
     *
     * @param departmentName the department name
     * @param facultyId the faculty ID
     * @return the department response wrapped in ApiResponse
     */
    @GetMapping("/name/{departmentName}/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentByNameAndFacultyId(
            @PathVariable String departmentName,
            @PathVariable Long facultyId) {
        log.info("REST request to get Department with name: {} and faculty ID: {}", departmentName, facultyId);
        DepartmentResponse response = departmentService.getDepartmentByNameAndFacultyId(departmentName, facultyId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Searches for departments by name.
     *
     * @param name the name to search for
     * @return list of matching department responses wrapped in ApiResponse
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> searchDepartmentsByName(@RequestParam String name) {
        log.info("REST request to search Departments by name: {}", name);
        List<DepartmentResponse> responses = departmentService.searchDepartmentsByName(name);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves a department with its programs by ID.
     *
     * @param id the department ID
     * @return the department response with program information wrapped in ApiResponse
     */
    @GetMapping("/{id}/with-programs")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentWithProgramsById(@PathVariable Long id) {
        log.info("REST request to get Department with programs by ID: {}", id);
        DepartmentResponse response = departmentService.getDepartmentWithProgramsById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all departments with their programs by faculty ID.
     *
     * @param facultyId the faculty ID
     * @return list of department responses with program information wrapped in ApiResponse
     */
    @GetMapping("/faculty/{facultyId}/with-programs")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartmentsWithProgramsByFacultyId(
            @PathVariable Long facultyId) {
        log.info("REST request to get Departments with programs by faculty ID: {}", facultyId);
        List<DepartmentResponse> responses = departmentService.getDepartmentsWithProgramsByFacultyId(facultyId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Deletes a department by its ID.
     *
     * @param id the department ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        log.info("REST request to delete Department with ID: {}", id);
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
    }
}
