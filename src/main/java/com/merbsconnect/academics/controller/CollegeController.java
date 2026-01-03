package com.merbsconnect.academics.controller;

import com.merbsconnect.academics.dto.request.CreateCollegeRequest;
import com.merbsconnect.academics.dto.request.UpdateCollegeRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.CollegeResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.service.CollegeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing College entities.
 */
@RestController
@RequestMapping("/api/v1/colleges")
@RequiredArgsConstructor
@Slf4j
public class CollegeController {

    private final CollegeService collegeService;

    /**
     * Creates a new college.
     *
     * @param request the college creation request
     * @return the created college response wrapped in ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CollegeResponse>> createCollege(@Valid @RequestBody CreateCollegeRequest request) {
        log.info("REST request to create College: {}", request);
        CollegeResponse response = collegeService.createCollege(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("College created successfully", response));
    }

    /**
     * Updates an existing college.
     *
     * @param id the college ID
     * @param request the college update request
     * @return the updated college response wrapped in ApiResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CollegeResponse>> updateCollege(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCollegeRequest request) {
        log.info("REST request to update College with ID: {}", id);
        CollegeResponse response = collegeService.updateCollege(id, request);
        return ResponseEntity.ok(ApiResponse.success("College updated successfully", response));
    }

    /**
     * Retrieves a college by its ID.
     *
     * @param id the college ID
     * @return the college response wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CollegeResponse>> getCollegeById(@PathVariable Long id) {
        log.info("REST request to get College with ID: {}", id);
        CollegeResponse response = collegeService.getCollegeById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves a college by its name.
     *
     * @param name the college name
     * @return the college response wrapped in ApiResponse
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<CollegeResponse>> getCollegeByName(@PathVariable String name) {
        log.info("REST request to get College with name: {}", name);
        CollegeResponse response = collegeService.getCollegeByName(name);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieves all colleges.
     *
     * @return list of all college responses wrapped in ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CollegeResponse>>> getAllColleges() {
        log.info("REST request to get all Colleges");
        List<CollegeResponse> responses = collegeService.getAllColleges();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves all colleges with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of college responses wrapped in ApiResponse
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<CollegeResponse>>> getAllCollegesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all Colleges with pagination - page: {}, size: {}", page, size);
        PageResponse<CollegeResponse> response = collegeService.getAllColleges(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Searches for colleges by name.
     *
     * @param name the name to search for
     * @return list of matching college responses wrapped in ApiResponse
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CollegeResponse>>> searchCollegesByName(@RequestParam String name) {
        log.info("REST request to search Colleges by name: {}", name);
        List<CollegeResponse> responses = collegeService.searchCollegesByName(name);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves all colleges with their faculties.
     *
     * @return list of college responses with faculty information wrapped in ApiResponse
     */
    @GetMapping("/with-faculties")
    public ResponseEntity<ApiResponse<List<CollegeResponse>>> getAllCollegesWithFaculties() {
        log.info("REST request to get all Colleges with faculties");
        List<CollegeResponse> responses = collegeService.getAllCollegesWithFaculties();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Retrieves a college with its faculties by ID.
     *
     * @param id the college ID
     * @return the college response with faculty information wrapped in ApiResponse
     */
    @GetMapping("/{id}/with-faculties")
    public ResponseEntity<ApiResponse<CollegeResponse>> getCollegeWithFacultiesById(@PathVariable Long id) {
        log.info("REST request to get College with faculties by ID: {}", id);
        CollegeResponse response = collegeService.getCollegeWithFacultiesById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Deletes a college by its ID.
     *
     * @param id the college ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCollege(@PathVariable Long id) {
        log.info("REST request to delete College with ID: {}", id);
        collegeService.deleteCollege(id);
        return ResponseEntity.ok(ApiResponse.success("College deleted successfully", null));
    }
}
