package com.merbsconnect.academics.service.impl;

import com.merbsconnect.academics.domain.Department;
import com.merbsconnect.academics.domain.Faculty;
import com.merbsconnect.academics.dto.request.CreateDepartmentRequest;
import com.merbsconnect.academics.dto.request.UpdateDepartmentRequest;
import com.merbsconnect.academics.dto.response.DepartmentResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.exception.DuplicateResourceException;
import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.academics.repository.DepartmentRepository;
import com.merbsconnect.academics.repository.FacultyRepository;
import com.merbsconnect.academics.service.DepartmentService;
import com.merbsconnect.util.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the DepartmentService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    @Override
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        log.info("Creating new department with name: {} for faculty ID: {}", 
                request.getDepartmentName(), request.getFacultyId());
        
        // Check if department with the same name already exists in the faculty
        if (existsByNameAndFacultyId(request.getDepartmentName(), request.getFacultyId())) {
            throw new DuplicateResourceException("Department with name " + request.getDepartmentName() + 
                    " already exists in the specified faculty");
        }
        
        // Find the faculty
        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + request.getFacultyId()));
        
        // Create and save the new department
        Department department = EntityMapper.toDepartmentEntity(request, faculty);
        Department savedDepartment = departmentRepository.save(department);
        
        log.info("Department created successfully with ID: {}", savedDepartment.getId());
        return EntityMapper.toDepartmentResponse(savedDepartment);
    }

    @Override
    public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request) {
        log.info("Updating department with ID: {}", id);
        
        // Find the department to update
        Department department = findDepartmentById(id);
        
        // Check if the new name is already taken by another department in the same faculty
        Long facultyId = request.getFacultyId() != null ? request.getFacultyId() : department.getFaculty().getId();
        if (request.getDepartmentName() != null && 
            !request.getDepartmentName().equals(department.getDepartmentName()) && 
            existsByNameAndFacultyId(request.getDepartmentName(), facultyId)) {
            throw new DuplicateResourceException("Department with name " + request.getDepartmentName() + 
                    " already exists in the specified faculty");
        }
        
        // Find the faculty if facultyId is provided
        Faculty faculty = null;
        if (request.getFacultyId() != null) {
            faculty = facultyRepository.findById(request.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + request.getFacultyId()));
        }
        
        // Update the department
        EntityMapper.updateDepartmentFromRequest(department, request, faculty);
        
        Department updatedDepartment = departmentRepository.save(department);
        log.info("Department updated successfully with ID: {}", updatedDepartment.getId());
        
        return EntityMapper.toDepartmentResponse(updatedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        log.info("Retrieving department with ID: {}", id);
        Department department = findDepartmentById(id);
        return EntityMapper.toDepartmentResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        log.info("Retrieving all departments");
        List<Department> departments = departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "departmentName"));
        return departments.stream()
                .map(EntityMapper::toDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> getAllDepartments(int page, int size) {
        log.info("Retrieving all departments with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "departmentName"));
        Page<Department> departmentPage = departmentRepository.findAll(pageable);
        
        return EntityMapper.mapPageResponse(departmentPage, EntityMapper::toDepartmentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartmentsByFacultyId(Long facultyId) {
        log.info("Retrieving departments by faculty ID: {}", facultyId);
        
        // Check if the faculty exists
        if (!facultyRepository.existsById(facultyId)) {
            throw new ResourceNotFoundException("Faculty not found with ID: " + facultyId);
        }
        
        List<Department> departments = departmentRepository.findByFacultyId(facultyId);
        return departments.stream()
                .map(EntityMapper::toDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartmentsByCollegeId(Long collegeId) {
        log.info("Retrieving departments by college ID: {}", collegeId);
        List<Department> departments = departmentRepository.findByCollegeId(collegeId);
        return departments.stream()
                .map(EntityMapper::toDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentByNameAndFacultyId(String departmentName, Long facultyId) {
        log.info("Retrieving department with name: {} and faculty ID: {}", departmentName, facultyId);
        Department department = departmentRepository.findByDepartmentNameAndFacultyId(departmentName, facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with name: " + departmentName + 
                        " and faculty ID: " + facultyId));
        return EntityMapper.toDepartmentResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> searchDepartmentsByName(String name) {
        log.info("Searching departments by name containing: {}", name);
        List<Department> departments = departmentRepository.findByDepartmentNameIgnoreCaseContaining(name);
        return departments.stream()
                .map(EntityMapper::toDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentWithProgramsById(Long id) {
        log.info("Retrieving department with programs by ID: {}", id);
        List<Department> departments = departmentRepository.findByIdWithPrograms(id);
        
        if (departments.isEmpty()) {
            throw new ResourceNotFoundException("Department not found with ID: " + id);
        }
        
        return EntityMapper.toDepartmentResponse(departments.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartmentsWithProgramsByFacultyId(Long facultyId) {
        log.info("Retrieving departments with programs by faculty ID: {}", facultyId);
        
        // Check if the faculty exists
        if (!facultyRepository.existsById(facultyId)) {
            throw new ResourceNotFoundException("Faculty not found with ID: " + facultyId);
        }
        
        List<Department> departments = departmentRepository.findByFacultyIdWithPrograms(facultyId);
        return departments.stream()
                .map(EntityMapper::toDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDepartment(Long id) {
        log.info("Deleting department with ID: {}", id);
        Department department = findDepartmentById(id);
        
        // Check if the department has associated programs
        if (!department.getPrograms().isEmpty()) {
            throw new IllegalStateException("Cannot delete department with ID " + id + " because it has associated programs");
        }
        
        departmentRepository.delete(department);
        log.info("Department deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndFacultyId(String departmentName, Long facultyId) {
        return departmentRepository.existsByDepartmentNameAndFacultyId(departmentName, facultyId);
    }
    
    /**
     * Helper method to find a department by ID or throw an exception if not found.
     *
     * @param id the department ID
     * @return the found department entity
     * @throws ResourceNotFoundException if the department is not found
     */
    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }
}