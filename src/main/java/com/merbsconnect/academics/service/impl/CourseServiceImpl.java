package com.merbsconnect.academics.service.impl;

import com.merbsconnect.academics.domain.Course;
import com.merbsconnect.academics.domain.Department;
import com.merbsconnect.academics.domain.Program;
import com.merbsconnect.academics.dto.request.CreateCourseRequest;
import com.merbsconnect.academics.dto.request.UpdateCourseRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.CourseResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.exception.DuplicateResourceException;
import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.academics.repository.CourseRepository;
import com.merbsconnect.academics.repository.DepartmentRepository;
import com.merbsconnect.academics.repository.ProgramRepository;
import com.merbsconnect.academics.service.CourseService;
import com.merbsconnect.academics.util.ResourceMapper;
import com.merbsconnect.enums.Semester;
import com.merbsconnect.util.mapper.EntityMapper;
import com.merbsconnect.util.mapper.PaginationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the CourseService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;

    @Override
    public ApiResponse<CourseResponse> createCourse(CreateCourseRequest request) {
        log.info("Creating new course with code: {}", request.getCourseCode());
        
        // Check if course with the same code already exists
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            log.error("Course with code {} already exists", request.getCourseCode());
            throw new DuplicateResourceException("Course with code " + request.getCourseCode() + " already exists");
        }
        
        // Get department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> {
                    log.error("Department not found with ID: {}", request.getDepartmentId());
                    return new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId());
                });
        
        // Get programs if provided
        Set<Program> programs = new HashSet<>();
        if (request.getProgramIds() != null && !request.getProgramIds().isEmpty()) {
            programs = request.getProgramIds().stream()
                    .map(programId -> programRepository.findById(programId)
                            .orElseThrow(() -> {
                                log.error("Program not found with ID: {}", programId);
                                return new ResourceNotFoundException("Program not found with ID: " + programId);
                            }))
                    .collect(Collectors.toSet());
        }
        
        // Create and save the course
        Course course = ResourceMapper.toCourse(request, department, programs);
        Course savedCourse = courseRepository.save(course);
        
        log.info("Course created successfully with ID: {}", savedCourse.getId());
        return ApiResponse.success("Course created successfully", ResourceMapper.toCourseResponse(savedCourse));
    }

    @Override
    public ApiResponse<CourseResponse> updateCourse(Long id, UpdateCourseRequest request) {
        log.info("Updating course with ID: {}", id);
        
        // Find the course
        Course course = findCourseById(id);
        
        // Check if course code is being changed and if it already exists
        if (request.getCourseCode() != null && !request.getCourseCode().equals(course.getCourseCode()) 
                && courseRepository.existsByCourseCode(request.getCourseCode())) {
            log.error("Course with code {} already exists", request.getCourseCode());
            throw new DuplicateResourceException("Course with code " + request.getCourseCode() + " already exists");
        }
        
        // Update department if provided
        if (request.getDepartmentId() != null && !request.getDepartmentId().equals(course.getDepartment().getId())) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> {
                        log.error("Department not found with ID: {}", request.getDepartmentId());
                        return new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId());
                    });
            course.setDepartment(department);
        }
        
        // Update programs if provided
        if (request.getProgramIds() != null) {
            Set<Program> programs = request.getProgramIds().stream()
                    .map(programId -> programRepository.findById(programId)
                            .orElseThrow(() -> {
                                log.error("Program not found with ID: {}", programId);
                                return new ResourceNotFoundException("Program not found with ID: " + programId);
                            }))
                    .collect(Collectors.toSet());
            course.setPrograms(programs);
        }
        
        // Update other fields if provided
        if (request.getCourseCode() != null) {
            course.setCourseCode(request.getCourseCode());
        }
        if (request.getCourseName() != null) {
            course.setCourseName(request.getCourseName());
        }
        if (request.getCourseDescription() != null) {
            course.setCourseDescription(request.getCourseDescription());
        }
        if (request.getSemester() != null) {
            course.setSemester(request.getSemester());
        }
        
        // Save the updated course
        Course updatedCourse = courseRepository.save(course);
        
        log.info("Course updated successfully with ID: {}", updatedCourse.getId());
        return ApiResponse.success("Course updated successfully", ResourceMapper.toCourseResponse(updatedCourse));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CourseResponse> getCourseById(Long id) {
        log.info("Retrieving course with ID: {}", id);
        Course course = findCourseById(id);
        return ApiResponse.success(ResourceMapper.toCourseResponse(course));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CourseResponse> getCourseByCourseCode(String courseCode) {
        log.info("Retrieving course with code: {}", courseCode);
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> {
                    log.error("Course not found with code: {}", courseCode);
                    return new ResourceNotFoundException("Course not found with code: " + courseCode);
                });
        return ApiResponse.success(ResourceMapper.toCourseResponse(course));
    }

    @Override
    public ApiResponse<Void> deleteCourse(Long id) {
        log.info("Deleting course with ID: {}", id);
        
        // Check if course exists
        if (!courseRepository.existsById(id)) {
            log.error("Course not found with ID: {}", id);
            throw new ResourceNotFoundException("Course not found with ID: " + id);
        }
        
        courseRepository.deleteById(id);
        
        log.info("Course deleted successfully with ID: {}", id);
        return ApiResponse.success("Course deleted successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CourseResponse>> getAllCourses() {
        log.info("Retrieving all courses");
        List<Course> courses = courseRepository.findAll(Sort.by(Sort.Direction.ASC, "courseCode"));
        List<CourseResponse> courseResponses = courses.stream()
                .map(ResourceMapper::toCourseResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(courseResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<CourseResponse>> getAllCourses(int page, int size) {
        log.info("Retrieving all courses with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "courseCode"));
        Page<Course> coursePage = courseRepository.findAll(pageable);
        
        PageResponse<CourseResponse> pageResponse = PaginationMapper.mapToPageResponse(
                coursePage, ResourceMapper::toCourseResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CourseResponse>> getCoursesByDepartmentId(Long departmentId) {
        log.info("Retrieving courses by department ID: {}", departmentId);
        
        // Check if department exists
        if (!departmentRepository.existsById(departmentId)) {
            log.error("Department not found with ID: {}", departmentId);
            throw new ResourceNotFoundException("Department not found with ID: " + departmentId);
        }
        
        List<Course> courses = courseRepository.findByDepartmentId(departmentId);
        List<CourseResponse> courseResponses = courses.stream()
                .map(ResourceMapper::toCourseResponse)
                .collect(Collectors.toList());
        
        return ApiResponse.success(courseResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CourseResponse>> getCoursesBySemester(Semester semester) {
        log.info("Retrieving courses by semester: {}", semester);
        List<Course> courses = courseRepository.findBySemester(semester);
        List<CourseResponse> courseResponses = courses.stream()
                .map(ResourceMapper::toCourseResponse)
                .collect(Collectors.toList());
        
        return ApiResponse.success(courseResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CourseResponse>> getCoursesByDepartmentIdAndSemester(Long departmentId, Semester semester) {
        log.info("Retrieving courses by department ID: {} and semester: {}", departmentId, semester);
        
        // Check if department exists
        if (!departmentRepository.existsById(departmentId)) {
            log.error("Department not found with ID: {}", departmentId);
            throw new ResourceNotFoundException("Department not found with ID: " + departmentId);
        }
        
        List<Course> courses = courseRepository.findByDepartmentIdAndSemester(departmentId, semester);
        List<CourseResponse> courseResponses = courses.stream()
                .map(ResourceMapper::toCourseResponse)
                .collect(Collectors.toList());
        
        return ApiResponse.success(courseResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CourseResponse>> getCoursesByFacultyId(Long facultyId) {
        log.info("Retrieving courses by faculty ID: {}", facultyId);
        List<Course> courses = courseRepository.findByFacultyId(facultyId);
        List<CourseResponse> courseResponses = courses.stream()
                .map(ResourceMapper::toCourseResponse)
                .collect(Collectors.toList());
        
        return ApiResponse.success(courseResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CourseResponse>> getCoursesByCollegeId(Long collegeId) {
        log.info("Retrieving courses by college ID: {}", collegeId);
        List<Course> courses = courseRepository.findByCollegeId(collegeId);
        List<CourseResponse> courseResponses = courses.stream()
                .map(ResourceMapper::toCourseResponse)
                .collect(Collectors.toList());
        
        return ApiResponse.success(courseResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CourseResponse>> getCoursesByProgramId(Long programId) {
        log.info("Retrieving courses by program ID: {}", programId);
        List<Course> courses = courseRepository.findByProgramsId(programId);
        List<CourseResponse> courseResponses = courses.stream()
                .map(ResourceMapper::toCourseResponse)
                .collect(Collectors.toList());
        
        return ApiResponse.success(courseResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Semester>> getDistinctSemesters() {
        log.info("Retrieving distinct semesters");
        List<Semester> semesters = courseRepository.findDistinctSemesters();
        return ApiResponse.success(semesters);
    }
    
    /**
     * Helper method to find a course by ID.
     *
     * @param id the course ID
     * @return the course entity
     * @throws ResourceNotFoundException if course not found
     */
    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found with ID: {}", id);
                    return new ResourceNotFoundException("Course not found with ID: " + id);
                });
    }
}
