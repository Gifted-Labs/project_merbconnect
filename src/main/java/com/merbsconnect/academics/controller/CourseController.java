package com.merbsconnect.academics.controller;

import com.merbsconnect.academics.dto.request.CreateCourseRequest;
import com.merbsconnect.academics.dto.request.UpdateCourseRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.CourseResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.service.CourseService;
import com.merbsconnect.enums.Semester;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Course entities.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/courses")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    /**
     * Creates a new course.
     *
     * @param request the course creation request
     * @return the created course response wrapped in ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        log.info("REST request to create Course: {}", request);
        ApiResponse<CourseResponse> response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", response.getData()));
    }

    /**
     * Updates an existing course.
     *
     * @param id the course ID
     * @param request the course update request
     * @return the updated course response wrapped in ApiResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {
        log.info("REST request to update Course with ID: {}", id);
        ApiResponse<CourseResponse> response = courseService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", response.getData()));
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param id the course ID
     * @return the course response wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        log.info("REST request to get Course with ID: {}", id);
        ApiResponse<CourseResponse> response = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves a course by its course code.
     *
     * @param courseCode the course code
     * @return the course response wrapped in ApiResponse
     */
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseByCourseCode(@PathVariable String courseCode) {
        log.info("REST request to get Course with code: {}", courseCode);
        ApiResponse<CourseResponse> response = courseService.getCourseByCourseCode(courseCode);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves all courses.
     *
     * @return list of all course responses wrapped in ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        log.info("REST request to get all Courses");
        ApiResponse<List<CourseResponse>> response = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves all courses with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of course responses wrapped in ApiResponse
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getAllCoursesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all Courses with pagination - page: {}, size: {}", page, size);
        ApiResponse<PageResponse<CourseResponse>> response = courseService.getAllCourses(page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves courses by department ID.
     *
     * @param departmentId the department ID
     * @return list of course responses wrapped in ApiResponse
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByDepartmentId(@PathVariable Long departmentId) {
        log.info("REST request to get Courses by department ID: {}", departmentId);
        ApiResponse<List<CourseResponse>> response = courseService.getCoursesByDepartmentId(departmentId);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves courses by semester.
     *
     * @param semester the semester
     * @return list of course responses wrapped in ApiResponse
     */
    @GetMapping("/semester/{semester}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesBySemester(@PathVariable Semester semester) {
        log.info("REST request to get Courses by semester: {}", semester);
        ApiResponse<List<CourseResponse>> response = courseService.getCoursesBySemester(semester);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves courses by department ID and semester.
     *
     * @param departmentId the department ID
     * @param semester the semester
     * @return list of course responses wrapped in ApiResponse
     */
    @GetMapping("/department/{departmentId}/semester/{semester}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByDepartmentIdAndSemester(
            @PathVariable Long departmentId,
            @PathVariable Semester semester) {
        log.info("REST request to get Courses by department ID: {} and semester: {}", departmentId, semester);
        ApiResponse<List<CourseResponse>> response = courseService.getCoursesByDepartmentIdAndSemester(departmentId, semester);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves courses by faculty ID.
     *
     * @param facultyId the faculty ID
     * @return list of course responses wrapped in ApiResponse
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByFacultyId(@PathVariable Long facultyId) {
        log.info("REST request to get Courses by faculty ID: {}", facultyId);
        ApiResponse<List<CourseResponse>> response = courseService.getCoursesByFacultyId(facultyId);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves courses by college ID.
     *
     * @param collegeId the college ID
     * @return list of course responses wrapped in ApiResponse
     */
    @GetMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByCollegeId(@PathVariable Long collegeId) {
        log.info("REST request to get Courses by college ID: {}", collegeId);
        ApiResponse<List<CourseResponse>> response = courseService.getCoursesByCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves courses by program ID.
     *
     * @param programId the program ID
     * @return list of course responses wrapped in ApiResponse
     */
    @GetMapping("/program/{programId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByProgramId(@PathVariable Long programId) {
        log.info("REST request to get Courses by program ID: {}", programId);
        ApiResponse<List<CourseResponse>> response = courseService.getCoursesByProgramId(programId);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves all distinct semesters.
     *
     * @return list of distinct semesters wrapped in ApiResponse
     */
    @GetMapping("/semesters")
    public ResponseEntity<ApiResponse<List<Semester>>> getDistinctSemesters() {
        log.info("REST request to get all distinct semesters");
        ApiResponse<List<Semester>> response = courseService.getDistinctSemesters();
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id the course ID
     * @return success message wrapped in ApiResponse
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        log.info("REST request to delete Course with ID: {}", id);
        ApiResponse<Void> response = courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
    }
}