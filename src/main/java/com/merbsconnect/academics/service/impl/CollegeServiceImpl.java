package com.merbsconnect.academics.service.impl;

import com.merbsconnect.academics.domain.College;
import com.merbsconnect.academics.dto.request.CreateCollegeRequest;
import com.merbsconnect.academics.dto.request.UpdateCollegeRequest;
import com.merbsconnect.academics.dto.response.CollegeResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.exception.DuplicateResourceException;
import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.academics.repository.CollegeRepository;
import com.merbsconnect.academics.service.CollegeService;
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
 * Implementation of the CollegeService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;

    @Override
    @Transactional
    public CollegeResponse createCollege(CreateCollegeRequest request) {
        log.info("Creating new college with name: {}", request.getCollegeName());
        
        // Check if college with the same name already exists
        if (existsByName(request.getCollegeName())) {
            throw new DuplicateResourceException("College with name " + request.getCollegeName() + " already exists");
        }
        
        // Create and save the new college
        College college = EntityMapper.toCollegeEntity(request);
        College savedCollege = collegeRepository.save(college);
        
        log.info("College created successfully with ID: {}", savedCollege.getId());
        return EntityMapper.toCollegeResponse(savedCollege);
    }

    @Override
    public CollegeResponse updateCollege(Long id, UpdateCollegeRequest request) {
        log.info("Updating college with ID: {}", id);
        
        // Find the college to update
        College college = findCollegeById(id);
        
        // Check if the new name is already taken by another college
        if (request.getCollegeName() != null && 
            !request.getCollegeName().equals(college.getCollegeName()) && 
            existsByName(request.getCollegeName())) {
            throw new DuplicateResourceException("College with name " + request.getCollegeName() + " already exists");
        }
        
        // Update the college
        if (request.getCollegeName() != null) {
            college.setCollegeName(request.getCollegeName());
        }
        
        College updatedCollege = collegeRepository.save(college);
        log.info("College updated successfully with ID: {}", updatedCollege.getId());
        
        return EntityMapper.toCollegeResponse(updatedCollege);
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeResponse getCollegeById(Long id) {
        log.info("Retrieving college with ID: {}", id);
        College college = findCollegeById(id);
        return EntityMapper.toCollegeResponse(college);
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeResponse getCollegeByName(String collegeName) {
        log.info("Retrieving college with name: {}", collegeName);
        College college = collegeRepository.findByCollegeName(collegeName)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with name: " + collegeName));
        return EntityMapper.toCollegeResponse(college);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeResponse> getAllColleges() {
        log.info("Retrieving all colleges");
        List<College> colleges = collegeRepository.findAll(Sort.by(Sort.Direction.ASC, "collegeName"));
        return colleges.stream()
                .map(EntityMapper::toCollegeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CollegeResponse> getAllColleges(int page, int size) {
        log.info("Retrieving all colleges with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "collegeName"));
        Page<College> collegePage = collegeRepository.findAll(pageable);
        
        return EntityMapper.mapPageResponse(collegePage, EntityMapper::toCollegeResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeResponse> searchCollegesByName(String name) {
        log.info("Searching colleges by name containing: {}", name);
        List<College> colleges = collegeRepository.findAllByCollegeNameIgnoreCaseContaining(name);
        return colleges.stream()
                .map(EntityMapper::toCollegeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeResponse> getAllCollegesWithFaculties() {
        log.info("Retrieving all colleges with faculties");
        List<College> colleges = collegeRepository.findAllWithFaculties();
        return colleges.stream()
                .map(EntityMapper::toCollegeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeResponse getCollegeWithFacultiesById(Long id) {
        log.info("Retrieving college with faculties by ID: {}", id);
        College college = collegeRepository.findByIdWithFaculties(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + id));
        return EntityMapper.toCollegeResponse(college);
    }

    @Override
    public void deleteCollege(Long id) {
        log.info("Deleting college with ID: {}", id);
        College college = findCollegeById(id);
        
        // Check if the college has associated faculties
        if (!college.getFaculties().isEmpty()) {
            throw new IllegalStateException("Cannot delete college with ID " + id + " because it has associated faculties");
        }
        
        collegeRepository.delete(college);
        log.info("College deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String collegeName) {
        return collegeRepository.existsByCollegeName(collegeName);
    }
    
    /**
     * Helper method to find a college by ID or throw an exception if not found.
     *
     * @param id the college ID
     * @return the found college entity
     * @throws ResourceNotFoundException if the college is not found
     */
    private College findCollegeById(Long id) {
        return collegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + id));
    }
}