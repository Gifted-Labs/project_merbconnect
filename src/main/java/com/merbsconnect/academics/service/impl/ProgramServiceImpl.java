package com.merbsconnect.academics.service.impl;

import com.merbsconnect.academics.domain.Course;
import com.merbsconnect.academics.domain.Department;
import com.merbsconnect.academics.domain.Program;
import com.merbsconnect.academics.dto.request.CreateProgramRequest;
import com.merbsconnect.academics.dto.request.UpdateProgramRequest;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ProgramResponse;
import com.merbsconnect.academics.exception.DuplicateResourceException;
import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.academics.repository.CourseRepository;
import com.merbsconnect.academics.repository.DepartmentRepository;
import com.merbsconnect.academics.repository.ProgramRepository;
import com.merbsconnect.academics.service.ProgramService;
import com.merbsconnect.util.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the ProgramService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    @Override
    public ProgramResponse createProgram(CreateProgramRequest request) {
        log.info("Creating new program with name: {} and code: {} for department ID: {}", 
                request.getProgramName(), request.getProgramCode(), request.getDepartmentId());
        
        // Check if program with the same name already exists in the department
        if (existsByNameAndDepartmentId(request.getProgramName(), request.getDepartmentId())) {
            throw new DuplicateResourceException("Program with name " + request.getProgramName() + 
                    " already exists in the specified department");
        }
        
        // Check if program with the same code already exists
        if (existsByCode(request.getProgramCode())) {
            throw new DuplicateResourceException("Program with code " + request.getProgramCode() + " already exists");
        }
        
        // Find the department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));
        
        // Find courses if course IDs are provided
        Set<Course> courses = Collections.emptySet();
        if (request.getCourseIds() != null && !request.getCourseIds().isEmpty()) {
            courses = new HashSet<>(courseRepository.findAllById(request.getCourseIds()));
            if (courses.size() != request.getCourseIds().size()) {
                throw new ResourceNotFoundException("One or more courses not found");
            }
        }
        
        // Create and save the new program
        Program program = EntityMapper.toProgramEntity(request, department, courses);
        Program savedProgram = programRepository.save(program);
        
        log.info("Program created successfully with ID: {}", savedProgram.getId());
        return EntityMapper.toProgramResponse(savedProgram);
    }

    @Override
    public ProgramResponse updateProgram(Long id, UpdateProgramRequest request) {
        log.info("Updating program with ID: {}", id);
        
        // Find the program to update
        Program program = findProgramById(id);
        
        // Check if the new name is already taken by another program in the same department
        Long departmentId = request.getDepartmentId() != null ? request.getDepartmentId() : program.getDepartment().getId();
        if (request.getProgramName() != null && 
            !request.getProgramName().equals(program.getProgramName()) && 
            existsByNameAndDepartmentId(request.getProgramName(), departmentId)) {
            throw new DuplicateResourceException("Program with name " + request.getProgramName() + 
                    " already exists in the specified department");
        }
        
        // Check if the new code is already taken by another program
        if (request.getProgramCode() != null && 
            !request.getProgramCode().equals(program.getProgramCode()) && 
            existsByCode(request.getProgramCode())) {
            throw new DuplicateResourceException("Program with code " + request.getProgramCode() + " already exists");
        }
        
        // Find the department if departmentId is provided
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));
        }
        
        // Find courses if course IDs are provided
        Set<Course> courses = null;
        if (request.getCourseIds() != null) {
            courses = new HashSet<>(courseRepository.findAllById(request.getCourseIds()));
            if (!request.getCourseIds().isEmpty() && courses.size() != request.getCourseIds().size()) {
                throw new ResourceNotFoundException("One or more courses not found");
            }
        }
        
        // Update the program
        EntityMapper.updateProgramFromRequest(program, request, department, courses);
        
        Program updatedProgram = programRepository.save(program);
        log.info("Program updated successfully with ID: {}", updatedProgram.getId());
        
        return EntityMapper.toProgramResponse(updatedProgram);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramResponse getProgramById(Long id) {
        log.info("Retrieving program with ID: {}", id);
        Program program = findProgramById(id);
        return EntityMapper.toProgramResponse(program);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramResponse getProgramByCode(String programCode) {
        log.info("Retrieving program with code: {}", programCode);
        Program program = programRepository.findByProgramCode(programCode)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with code: " + programCode));
        return EntityMapper.toProgramResponse(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> getAllPrograms() {
        log.info("Retrieving all programs");
        List<Program> programs = programRepository.findAll(Sort.by(Sort.Direction.ASC, "programName"));
        return programs.stream()
                .map(EntityMapper::toProgramResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProgramResponse> getAllPrograms(int page, int size) {
        log.info("Retrieving all programs with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "programName"));
        Page<Program> programPage = programRepository.findAll(pageable);
        
        return EntityMapper.mapPageResponse(programPage, EntityMapper::toProgramResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> getProgramsByDepartmentId(Long departmentId) {
        log.info("Retrieving programs by department ID: {}", departmentId);
        
        // Check if the department exists
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found with ID: " + departmentId);
        }
        
        List<Program> programs = programRepository.findByDepartmentId(departmentId);
        return programs.stream()
                .map(EntityMapper::toProgramResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> getProgramsByFacultyId(Long facultyId) {
        log.info("Retrieving programs by faculty ID: {}", facultyId);
        List<Program> programs = programRepository.findByFacultyId(facultyId);
        return programs.stream()
                .map(EntityMapper::toProgramResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> getProgramsByCollegeId(Long collegeId) {
        log.info("Retrieving programs by college ID: {}", collegeId);
        List<Program> programs = programRepository.findByCollegeId(collegeId);
        return programs.stream()
                .map(EntityMapper::toProgramResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramResponse getProgramByNameAndDepartmentId(String programName, Long departmentId) {
        log.info("Retrieving program with name: {} and department ID: {}", programName, departmentId);
        Program program = programRepository.findByProgramNameAndDepartmentId(programName, departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with name: " + programName + 
                        " and department ID: " + departmentId));
        return EntityMapper.toProgramResponse(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> searchProgramsByName(String name) {
        log.info("Searching programs by name containing: {}", name);
        List<Program> programs = programRepository.findByProgramNameContainingIgnoreCase(name);
        return programs.stream()
                .map(EntityMapper::toProgramResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProgram(Long id) {
        log.info("Deleting program with ID: {}", id);
        Program program = findProgramById(id);
        
        // Remove program from all associated courses
        program.getCourses().forEach(course -> course.getPrograms().remove(program));
        
        programRepository.delete(program);
        log.info("Program deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndDepartmentId(String programName, Long departmentId) {
        return programRepository.existsByProgramNameAndDepartmentId(programName, departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String programCode) {
        return programRepository.existsByProgramCode(programCode);
    }

    @Override
    public ProgramResponse addCourseToProgram(Long programId, Long courseId) {
        log.info("Adding course with ID: {} to program with ID: {}", courseId, programId);
        
        Program program = findProgramById(programId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        // Check if the course is already in the program
        if (program.getCourses().contains(course)) {
            log.info("Course with ID: {} is already in program with ID: {}", courseId, programId);
            return EntityMapper.toProgramResponse(program);
        }
        
        program.getCourses().add(course);
        course.getPrograms().add(program);
        
        Program updatedProgram = programRepository.save(program);
        log.info("Course added successfully to program");
        
        return EntityMapper.toProgramResponse(updatedProgram);
    }

    @Override
    public ProgramResponse removeCourseFromProgram(Long programId, Long courseId) {
        log.info("Removing course with ID: {} from program with ID: {}", courseId, programId);
        
        Program program = findProgramById(programId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        // Check if the course is in the program
        if (!program.getCourses().contains(course)) {
            log.info("Course with ID: {} is not in program with ID: {}", courseId, programId);
            return EntityMapper.toProgramResponse(program);
        }
        
        program.getCourses().remove(course);
        course.getPrograms().remove(program);
        
        Program updatedProgram = programRepository.save(program);
        log.info("Course removed successfully from program");
        
        return EntityMapper.toProgramResponse(updatedProgram);
    }
    
    /**
     * Helper method to find a program by ID or throw an exception if not found.
     *
     * @param id the program ID
     * @return the found program entity
     * @throws ResourceNotFoundException if the program is not found
     */
    private Program findProgramById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + id));
    }
}