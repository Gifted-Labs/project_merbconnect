package com.merbsconnect.academics.service.impl;

import com.merbsconnect.academics.domain.College;
import com.merbsconnect.academics.domain.Faculty;
import com.merbsconnect.academics.dto.request.CreateFacultyRequest;
import com.merbsconnect.academics.dto.request.UpdateFacultyRequest;
import com.merbsconnect.academics.dto.response.FacultyResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.exception.DuplicateResourceException;
import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.academics.repository.CollegeRepository;
import com.merbsconnect.academics.repository.FacultyRepository;
import com.merbsconnect.academics.service.FacultyService;
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
 * Implementation of the FacultyService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final CollegeRepository collegeRepository;

    @Override
    public FacultyResponse createFaculty(CreateFacultyRequest request) {
        log.info("Creating new faculty with name: {} for college ID: {}", 
                request.getFacultyName(), request.getCollegeId());
        
        // Check if faculty with the same name already exists in the college
        if (existsByNameAndCollegeId(request.getFacultyName(), request.getCollegeId())) {
            throw new DuplicateResourceException("Faculty with name " + request.getFacultyName() + 
                    " already exists in the specified college");
        }
        
        // Find the college
        College college = collegeRepository.findById(request.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + request.getCollegeId()));
        
        // Create and save the new faculty
        Faculty faculty = EntityMapper.toFacultyEntity(request, college);
        Faculty savedFaculty = facultyRepository.save(faculty);
        
        log.info("Faculty created successfully with ID: {}", savedFaculty.getId());
        return EntityMapper.toFacultyResponse(savedFaculty);
    }

    @Override
    public FacultyResponse updateFaculty(Long id, UpdateFacultyRequest request) {
        log.info("Updating faculty with ID: {}", id);
        
        // Find the faculty to update
        Faculty faculty = findFacultyById(id);
        
        // Check if the new name is already taken by another faculty in the same college
        Long collegeId = request.getCollegeId() != null ? request.getCollegeId() : faculty.getCollege().getId();
        if (request.getFacultyName() != null && 
            !request.getFacultyName().equals(faculty.getFacultyName()) && 
            existsByNameAndCollegeId(request.getFacultyName(), collegeId)) {
            throw new DuplicateResourceException("Faculty with name " + request.getFacultyName() + 
                    " already exists in the specified college");
        }
        
        // Find the college if collegeId is provided
        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + request.getCollegeId()));
        }
        
        // Update the faculty
        EntityMapper.updateFacultyFromRequest(faculty, request, college);
        
        Faculty updatedFaculty = facultyRepository.save(faculty);
        log.info("Faculty updated successfully with ID: {}", updatedFaculty.getId());
        
        return EntityMapper.toFacultyResponse(updatedFaculty);
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyResponse getFacultyById(Long id) {
        log.info("Retrieving faculty with ID: {}", id);
        Faculty faculty = findFacultyById(id);
        return EntityMapper.toFacultyResponse(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponse> getAllFaculties() {
        log.info("Retrieving all faculties");
        List<Faculty> faculties = facultyRepository.findAll(Sort.by(Sort.Direction.ASC, "facultyName"));
        return faculties.stream()
                .map(EntityMapper::toFacultyResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FacultyResponse> getAllFaculties(int page, int size) {
        log.info("Retrieving all faculties with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "facultyName"));
        Page<Faculty> facultyPage = facultyRepository.findAll(pageable);
        
        return EntityMapper.mapPageResponse(facultyPage, EntityMapper::toFacultyResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponse> getFacultiesByCollegeId(Long collegeId) {
        log.info("Retrieving faculties by college ID: {}", collegeId);
        
        // Check if the college exists
        if (!collegeRepository.existsById(collegeId)) {
            throw new ResourceNotFoundException("College not found with ID: " + collegeId);
        }
        
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + collegeId));
        
        List<Faculty> faculties = facultyRepository.findByCollege(college);
        return faculties.stream()
                .map(EntityMapper::toFacultyResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyResponse getFacultyByNameAndCollegeId(String facultyName, Long collegeId) {
        log.info("Retrieving faculty with name: {} and college ID: {}", facultyName, collegeId);
        Faculty faculty = facultyRepository.findByFacultyNameAndCollegeId(facultyName, collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with name: " + facultyName + 
                        " and college ID: " + collegeId));
        return EntityMapper.toFacultyResponse(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponse> searchFacultiesByName(String name) {
        log.info("Searching faculties by name containing: {}", name);
        List<Faculty> faculties = facultyRepository.findByFacultyNameIgnoreCaseContaining(name);
        return faculties.stream()
                .map(EntityMapper::toFacultyResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyResponse getFacultyWithDepartmentsById(Long id) {
        log.info("Retrieving faculty with departments by ID: {}", id);
        Faculty faculty = facultyRepository.findByIdWithDepartments(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + id));
        return EntityMapper.toFacultyResponse(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponse> getFacultiesWithDepartmentsByCollegeId(Long collegeId) {
        log.info("Retrieving faculties with departments by college ID: {}", collegeId);
        
        // Check if the college exists
        if (!collegeRepository.existsById(collegeId)) {
            throw new ResourceNotFoundException("College not found with ID: " + collegeId);
        }
        
        List<Faculty> faculties = facultyRepository.findByCollegeIdWithDepartments(collegeId);
        return faculties.stream()
                .map(EntityMapper::toFacultyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFaculty(Long id) {
        log.info("Deleting faculty with ID: {}", id);
        Faculty faculty = findFacultyById(id);
        
        // Check if the faculty has associated departments
        if (!faculty.getDepartments().isEmpty()) {
            throw new IllegalStateException("Cannot delete faculty with ID " + id + " because it has associated departments");
        }
        
        facultyRepository.delete(faculty);
        log.info("Faculty deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndCollegeId(String facultyName, Long collegeId) {
        return facultyRepository.existsByFacultyNameAndCollegeId(facultyName, collegeId);
    }
    
    /**
     * Helper method to find a faculty by ID or throw an exception if not found.
     *
     * @param id the faculty ID
     * @return the found faculty entity
     * @throws ResourceNotFoundException if the faculty is not found
     */
    private Faculty findFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + id));
    }
}