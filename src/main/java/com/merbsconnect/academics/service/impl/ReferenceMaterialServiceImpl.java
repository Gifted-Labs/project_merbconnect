package com.merbsconnect.academics.service.impl;

import com.merbsconnect.academics.domain.Course;
import com.merbsconnect.academics.domain.ReferenceMaterial;
import com.merbsconnect.academics.dto.request.CreateReferenceMaterialRequest;
import com.merbsconnect.academics.dto.request.UpdateReferenceMaterialRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.ReferenceMaterialResponse;
import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.academics.repository.CourseRepository;
import com.merbsconnect.academics.repository.ReferenceMaterialRepository;
import com.merbsconnect.academics.service.ReferenceMaterialService;
import com.merbsconnect.academics.util.ResourceMapper;
import com.merbsconnect.util.mapper.PaginationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the ReferenceMaterialService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReferenceMaterialServiceImpl implements ReferenceMaterialService {

    private final ReferenceMaterialRepository referenceMaterialRepository;
    private final CourseRepository courseRepository;

    @Override
    public ApiResponse<ReferenceMaterialResponse> createReferenceMaterial(CreateReferenceMaterialRequest request) {
        log.info("Creating new reference material with title: {}", request.getTitle());
        
        // Get course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> {
                    log.error("Course not found with ID: {}", request.getCourseId());
                    return new ResourceNotFoundException("Course not found with ID: " + request.getCourseId());
                });
        
        // Create reference material
        ReferenceMaterial referenceMaterial = ReferenceMaterial.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .course(course)
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .isbn(request.getIsbn())
                .edition(request.getEdition())
                .publicationYear(request.getPublicationYear())
                .language(request.getLanguage())
                .numberOfPages(request.getNumberOfPages())
                .format(request.getFormat())
                .fileSize(request.getFileSize())
                .fileUrl(request.getFileUrl())
                .downloadLink(request.getDownloadLink())
                .coverImageUrl(request.getCoverImageUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Save reference material
        ReferenceMaterial savedReferenceMaterial = referenceMaterialRepository.save(referenceMaterial);
        
        log.info("Reference material created successfully with ID: {}", savedReferenceMaterial.getId());
        return ApiResponse.success("Reference material created successfully", 
                ResourceMapper.toReferenceMaterialResponse(savedReferenceMaterial));
    }

    @Override
    public ApiResponse<ReferenceMaterialResponse> updateReferenceMaterial(Long id, UpdateReferenceMaterialRequest request) {
        log.info("Updating reference material with ID: {}", id);
        
        // Find reference material
        ReferenceMaterial referenceMaterial = findReferenceMaterialById(id);
        
        // Update course if provided
        if (request.getCourseId() != null && !request.getCourseId().equals(referenceMaterial.getCourse().getId())) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> {
                        log.error("Course not found with ID: {}", request.getCourseId());
                        return new ResourceNotFoundException("Course not found with ID: " + request.getCourseId());
                    });
            referenceMaterial.setCourse(course);
        }
        
        // Update other fields if provided
        if (request.getTitle() != null) {
            referenceMaterial.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            referenceMaterial.setDescription(request.getDescription());
        }
        if (request.getAuthor() != null) {
            referenceMaterial.setAuthor(request.getAuthor());
        }
        if (request.getPublisher() != null) {
            referenceMaterial.setPublisher(request.getPublisher());
        }
        if (request.getIsbn() != null) {
            referenceMaterial.setIsbn(request.getIsbn());
        }
        if (request.getEdition() != null) {
            referenceMaterial.setEdition(request.getEdition());
        }
        if (request.getPublicationYear() != null) {
            referenceMaterial.setPublicationYear(request.getPublicationYear());
        }
        if (request.getLanguage() != null) {
            referenceMaterial.setLanguage(request.getLanguage());
        }
        if (request.getNumberOfPages() != null) {
            referenceMaterial.setNumberOfPages(request.getNumberOfPages());
        }
        if (request.getFormat() != null) {
            referenceMaterial.setFormat(request.getFormat());
        }
        if (request.getFileSize() != null) {
            referenceMaterial.setFileSize(request.getFileSize());
        }
        if (request.getFileUrl() != null) {
            referenceMaterial.setFileUrl(request.getFileUrl());
        }
        if (request.getDownloadLink() != null) {
            referenceMaterial.setDownloadLink(request.getDownloadLink());
        }
        if (request.getCoverImageUrl() != null) {
            referenceMaterial.setCoverImageUrl(request.getCoverImageUrl());
        }
        
        // Update timestamp
        referenceMaterial.setUpdatedAt(LocalDateTime.now());
        
        // Save updated reference material
        ReferenceMaterial updatedReferenceMaterial = referenceMaterialRepository.save(referenceMaterial);
        
        log.info("Reference material updated successfully with ID: {}", updatedReferenceMaterial.getId());
        return ApiResponse.success("Reference material updated successfully", 
                ResourceMapper.toReferenceMaterialResponse(updatedReferenceMaterial));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ReferenceMaterialResponse> getReferenceMaterialById(Long id) {
        log.info("Retrieving reference material with ID: {}", id);
        ReferenceMaterial referenceMaterial = findReferenceMaterialById(id);
        return ApiResponse.success(ResourceMapper.toReferenceMaterialResponse(referenceMaterial));
    }

    @Override
    public ApiResponse<Void> deleteReferenceMaterial(Long id) {
        log.info("Deleting reference material with ID: {}", id);
        
        // Check if reference material exists
        if (!referenceMaterialRepository.existsById(id)) {
            log.error("Reference material not found with ID: {}", id);
            throw new ResourceNotFoundException("Reference material not found with ID: " + id);
        }
        
        referenceMaterialRepository.deleteById(id);
        
        log.info("Reference material deleted successfully with ID: {}", id);
        return ApiResponse.success("Reference material deleted successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ReferenceMaterialResponse>> getAllReferenceMaterials(int page, int size) {
        log.info("Retrieving all reference materials with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReferenceMaterial> referenceMaterialPage = referenceMaterialRepository.findAll(pageable);
        
        PageResponse<ReferenceMaterialResponse> pageResponse = PaginationMapper.mapToPageResponse(
                referenceMaterialPage, ResourceMapper::toReferenceMaterialResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByFormat(
            String format, int page, int size) {
        log.info("Retrieving reference materials by format: {} with pagination - page: {}, size: {}", 
                format, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReferenceMaterial> referenceMaterialPage = referenceMaterialRepository.findByFormat(format, pageable);
        
        PageResponse<ReferenceMaterialResponse> pageResponse = PaginationMapper.mapToPageResponse(
                referenceMaterialPage, ResourceMapper::toReferenceMaterialResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByCourseIdAndFormat(
            Long courseId, String format, int page, int size) {
        log.info("Retrieving reference materials by course ID: {} and format: {} with pagination - page: {}, size: {}", 
                courseId, format, page, size);
        
        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            log.error("Course not found with ID: {}", courseId);
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReferenceMaterial> referenceMaterialPage;
        
        if (format != null && !format.isEmpty()) {
            referenceMaterialPage = referenceMaterialRepository.findByCourseIdAndFormat(courseId, format, pageable);
        } else {
            // If format is not provided, find by course ID only
            referenceMaterialPage = referenceMaterialRepository.findByCourseId (courseId, pageable);
        }
        
        PageResponse<ReferenceMaterialResponse> pageResponse = PaginationMapper.mapToPageResponse(
                referenceMaterialPage, ResourceMapper::toReferenceMaterialResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByDepartmentIdAndFormat(
            Long departmentId, String format, int page, int size) {
        log.info("Retrieving reference materials by department ID: {} and format: {} with pagination - page: {}, size: {}", 
                departmentId, format, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReferenceMaterial> referenceMaterialPage = referenceMaterialRepository.findByDepartmentIdAndFormat(
                departmentId, format, pageable);
        
        PageResponse<ReferenceMaterialResponse> pageResponse = PaginationMapper.mapToPageResponse(
                referenceMaterialPage, ResourceMapper::toReferenceMaterialResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByLanguage(
            String language, int page, int size) {
        log.info("Retrieving reference materials by language: {} with pagination - page: {}, size: {}", 
                language, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Using a specification for the search
        Page<ReferenceMaterial> referenceMaterialPage = referenceMaterialRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("language"), language), pageable);
        
        PageResponse<ReferenceMaterialResponse> pageResponse = PaginationMapper.mapToPageResponse(
                referenceMaterialPage, ResourceMapper::toReferenceMaterialResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ReferenceMaterialResponse>> getReferenceMaterialsByPublicationYear(
            String publicationYear, int page, int size) {
        log.info("Retrieving reference materials by publication year: {} with pagination - page: {}, size: {}", 
                publicationYear, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Using a specification for the search
        Page<ReferenceMaterial> referenceMaterialPage = referenceMaterialRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("publicationYear"), publicationYear), pageable);
        
        PageResponse<ReferenceMaterialResponse> pageResponse = PaginationMapper.mapToPageResponse(
                referenceMaterialPage, ResourceMapper::toReferenceMaterialResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ReferenceMaterialResponse>> searchReferenceMaterialsByTitle(
            String keyword, int page, int size) {
        log.info("Searching reference materials by title containing: {} with pagination - page: {}, size: {}", 
                keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Using a specification for the search
        Page<ReferenceMaterial> referenceMaterialPage = referenceMaterialRepository.findAll(
                (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"), 
                pageable);
        
        PageResponse<ReferenceMaterialResponse> pageResponse = PaginationMapper.mapToPageResponse(
                referenceMaterialPage, ResourceMapper::toReferenceMaterialResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ReferenceMaterialResponse>> searchReferenceMaterialsByAuthor(
            String author, int page, int size) {
        log.info("Searching reference materials by author containing: {} with pagination - page: {}, size: {}", 
                author, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Using a specification for the search
        Page<ReferenceMaterial> referenceMaterialPage = referenceMaterialRepository.findAll(
                (root, query, cb) -> cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"), 
                pageable);
        
        PageResponse<ReferenceMaterialResponse> pageResponse = PaginationMapper.mapToPageResponse(
                referenceMaterialPage, ResourceMapper::toReferenceMaterialResponse);
        
        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<String>> getDistinctFormats() {
        log.info("Retrieving distinct formats");
        List<String> formats = referenceMaterialRepository.findDistinctFormats();
        return ApiResponse.success(formats);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<String>> getDistinctLanguages() {
        log.info("Retrieving distinct languages");
        List<String> languages = referenceMaterialRepository.findDistinctLanguages();
        return ApiResponse.success(languages);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<String>> getDistinctPublicationYears() {
        log.info("Retrieving distinct publication years");
        List<String> publicationYears = referenceMaterialRepository.findDistinctPublicationYears();
        return ApiResponse.success(publicationYears);
    }
    
    /**
     * Helper method to find a reference material by ID.
     *
     * @param id the reference material ID
     * @return the reference material entity
     * @throws ResourceNotFoundException if reference material not found
     */
    private ReferenceMaterial findReferenceMaterialById(Long id) {
        return referenceMaterialRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reference material not found with ID: {}", id);
                    return new ResourceNotFoundException("Reference material not found with ID: " + id);
                });
    }
}
