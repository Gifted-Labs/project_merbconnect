package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.request.CreateCourseRequest;
import com.merbsconnect.academics.dto.request.UpdateCourseRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.CourseMinimalResponse;
import com.merbsconnect.academics.dto.response.CourseResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.enums.Semester;

import java.util.List;

/**
 * Service interface for managing Course entities.
 */
public interface CourseService {
    
    /**
     * Creates a new course.
     *
     * @param request the course creation request
     * @return API response containing the created course
     */
    ApiResponse<CourseResponse> createCourse(CreateCourseRequest request);
    
    /**
     * Updates an existing course.
     *
     * @param id the course ID
     * @param request the course update request
     * @return API response containing the updated course
     */
    ApiResponse<CourseResponse> updateCourse(Long id, UpdateCourseRequest request);
    
    /**
     * Retrieves a course by its ID.
     *
     * @param id the course ID
     * @return API response containing the course
     */
    ApiResponse<CourseResponse> getCourseById(Long id);
    
    /**
     * Retrieves a course by its course code.
     *
     * @param courseCode the course code
     * @return API response containing the course
     */
    ApiResponse<CourseResponse> getCourseByCourseCode(String courseCode);
    
    /**
     * Deletes a course by its ID.
     *
     * @param id the course ID
     * @return API response indicating success or failure
     */
    ApiResponse<Void> deleteCourse(Long id);
    
    /**
     * Retrieves all courses.
     *
     * @return API response containing list of all courses
     */
    ApiResponse<List<CourseResponse>> getAllCourses();
    
    /**
     * Retrieves all courses with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated courses
     */
    ApiResponse<PageResponse<CourseResponse>> getAllCourses(int page, int size);
    
    /**
     * Retrieves courses by department ID.
     *
     * @param departmentId the department ID
     * @return API response containing list of courses
     */
    ApiResponse<List<CourseResponse>> getCoursesByDepartmentId(Long departmentId);
    
    /**
     * Retrieves courses by semester.
     *
     * @param semester the semester
     * @return API response containing list of courses
     */
    ApiResponse<List<CourseResponse>> getCoursesBySemester(Semester semester);
    
    /**
     * Retrieves courses by department ID and semester.
     *
     * @param departmentId the department ID
     * @param semester the semester
     * @return API response containing list of courses
     */
    ApiResponse<List<CourseResponse>> getCoursesByDepartmentIdAndSemester(Long departmentId, Semester semester);
    
    /**
     * Retrieves courses by faculty ID.
     *
     * @param facultyId the faculty ID
     * @return API response containing list of courses
     */
    ApiResponse<List<CourseResponse>> getCoursesByFacultyId(Long facultyId);
    
    /**
     * Retrieves courses by college ID.
     *
     * @param collegeId the college ID
     * @return API response containing list of courses
     */
    ApiResponse<List<CourseResponse>> getCoursesByCollegeId(Long collegeId);
    
    /**
     * Retrieves courses by program ID.
     *
     * @param programId the program ID
     * @return API response containing list of courses
     */
    ApiResponse<List<CourseResponse>> getCoursesByProgramId(Long programId);
    
    /**
     * Retrieves all distinct semesters.
     *
     * @return API response containing list of distinct semesters
     */
    ApiResponse<List<Semester>> getDistinctSemesters();
}
