package com.merbsconnect.academics.controller;

import com.merbsconnect.academics.dto.request.CreateFacultyRequest;
import com.merbsconnect.academics.dto.request.UpdateFacultyRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.FacultyResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Faculty entities.
 */
@RestController
@RequestMapping("/api/v1/faculties")
@RequiredArgsConstructor
@Slf4j
public class FacultyController {

    private final FacultyService facultyService;

    /**
     * Creates a new faculty.
     *
     * @param request the faculty creation request
     * @return the created faculty response wrapped in ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FacultyResponse>> createFaculty(@Valid @RequestBody CreateFacultyRequest request) {
        log.info("REST request to create Faculty: {}", request);
        FacultyResponse response = facultyService.createFaculty(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Faculty created successfully", response));
    }

    /**
     * Updates an existing faculty.
     *
     * @param id the faculty ID
     * @param request the faculty update request
     * @return the updated faculty response wrapped in ApiResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FacultyResponse>> updateFaculty(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFacultyRequest request) {
        log.info("REST request to update Faculty with ID: {}", id);
        FacultyResponse response = facultyService.updateFaculty(id, request);
        return ResponseEntity.ok(ApiResponse.success("Faculty updated successfully", response));
    }

    /**
     * Retrieves a faculty by its ID.
     *
     * @param id the faculty ID
     * @return the faculty response wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> getFacultyById(@PathVariable Long id) {
        log.info("REST request to get Faculty with ID: {}", id);
        FacultyResponse response = facultyService.getFacultyById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all faculties.
     *
     * @return list of all faculty responses wrapped in ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getAllFaculties() {
        log.info("REST request to get all Faculties");
        List<FacultyResponse> responses = facultyService.getAllFaculties();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves all faculties with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of faculty responses wrapped in ApiResponse
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<FacultyResponse>>> getAllFacultiesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all Faculties with pagination - page: {}, size: {}", page, size);
        PageResponse<FacultyResponse> response = facultyService.getAllFaculties(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all faculties by college ID.
     *
     * @param collegeId the college ID
     * @return list of faculty responses for the specified college wrapped in ApiResponse
     */
    @GetMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getFacultiesByCollegeId(@PathVariable Long collegeId) {
        log.info("REST request to get Faculties by college ID: {}", collegeId);
        List<FacultyResponse> responses = facultyService.getFacultiesByCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves a faculty by name and college ID.
     *
     * @param facultyName the faculty name
     * @param collegeId the college ID
     * @return the faculty response wrapped in ApiResponse
     */
    @GetMapping("/name")
    public ResponseEntity<ApiResponse<FacultyResponse>> getFacultyByNameAndCollegeId(
            @RequestParam String facultyName,
            @RequestParam Long collegeId) {
        log.info("REST request to get Faculty by name: {} and college ID: {}", facultyName, collegeId);
        FacultyResponse response = facultyService.getFacultyByNameAndCollegeId(facultyName, collegeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Searches for faculties by name.
     *
     * @param name the name to search for
     * @return list of matching faculty responses wrapped in ApiResponse
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> searchFacultiesByName(@RequestParam String name) {
        log.info("REST request to search Faculties by name: {}", name);
        List<FacultyResponse> responses = facultyService.searchFacultiesByName(name);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves a faculty with its departments by ID.
     *
     * @param id the faculty ID
     * @return the faculty response with department information wrapped in ApiResponse
     */
    @GetMapping("/{id}/with-departments")
    public ResponseEntity<ApiResponse<FacultyResponse>> getFacultyWithDepartmentsById(@PathVariable Long id) {
        log.info("REST request to get Faculty with departments by ID: {}", id);
        FacultyResponse response = facultyService.getFacultyWithDepartmentsById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all faculties with their departments by college ID.
     *
     * @param collegeId the college ID
     * @return list of faculty responses with department information wrapped in ApiResponse
     */
    @GetMapping("/college/{collegeId}/with-departments")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getFacultiesWithDepartmentsByCollegeId(
            @PathVariable Long collegeId) {
        log.info("REST request to get Faculties with departments by college ID: {}", collegeId);
        List<FacultyResponse> responses = facultyService.getFacultiesWithDepartmentsByCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Deletes a faculty by its ID.
     *
     * @param id the faculty ID
     * @return success message wrapped in ApiResponse
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFaculty(@PathVariable Long id) {
        log.info("REST request to delete Faculty with ID: {}", id);
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok(ApiResponse.success("Faculty deleted successfully", null));
    }

    /**
     * Checks if a faculty exists by name and college ID.
     *
     * @param facultyName the faculty name
     * @param collegeId the college ID
     * @return boolean indicating if the faculty exists wrapped in ApiResponse
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> existsByNameAndCollegeId(
            @RequestParam String facultyName,
            @RequestParam Long collegeId) {
        log.info("REST request to check if Faculty exists by name: {} and college ID: {}", facultyName, collegeId);
        boolean exists = facultyService.existsByNameAndCollegeId(facultyName, collegeId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
