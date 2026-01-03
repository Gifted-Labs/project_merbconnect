package com.merbsconnect.util.mapper;

import com.merbsconnect.academics.domain.*;
import com.merbsconnect.academics.dto.request.*;
import com.merbsconnect.academics.dto.response.*;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Comprehensive utility class for mapping between entities and DTOs.
 * This class provides static methods for all entity-to-DTO and DTO-to-entity conversions.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityMapper {

    // ==================== Generic Mapping Methods ====================
    
    /**
     * Maps a collection of entities to DTOs using the provided mapper function
     */
    public static <T, R> List<R> mapAll(Collection<T> entities, Function<T, R> mapper) {
        return entities.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
    
    /**
     * Maps a Page of entities to a Page of DTOs using the provided mapper function
     */
    public static <T, R> PageResponse<R> mapPageResponse(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
        
        return PageResponse.<R>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
    
    /**
     * Maps a Page of entities to a ResourceSearchResponse of DTOs using the provided mapper function
     */
    public static <T, R> ResourceSearchResponse<R> mapResourceSearchResponse(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
        
        return ResourceSearchResponse.<R>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    // ==================== Resource Mappings ====================
    
    /**
     * Maps a Resource entity to a ResourceSummaryResponse DTO
     */
    public static ResourceSummaryResponse toResourceSummary(Resource resource) {
        ResourceSummaryResponse.ResourceSummaryResponseBuilder builder = ResourceSummaryResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .resourceType(resource.getClass().getSimpleName())
                .courseId(resource.getCourse().getId())
                .courseCode(resource.getCourse().getCourseCode())
                .courseName(resource.getCourse().getCourseName())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt());
        
        // Add specific fields based on resource type
        if (resource instanceof Quiz) {
            addQuizSpecificFields((Quiz) resource, builder);
        } else if (resource instanceof ReferenceMaterial) {
            addReferenceMaterialSpecificFields((ReferenceMaterial) resource, builder);
        }

        return builder.build();
    }

    /**
     * Helper method to add Quiz-specific fields to a ResourceSummaryResponse builder
     */
    private static void addQuizSpecificFields(Quiz quiz, ResourceSummaryResponse.ResourceSummaryResponseBuilder builder) {
        builder.difficultyLevel(quiz.getDifficultyLevel())
               .yearGiven(quiz.getYearGiven())
               .quizType(quiz.getQuizType() != null ? quiz.getQuizType().name() : null);
    }

    /**
     * Helper method to add ReferenceMaterial-specific fields to a ResourceSummaryResponse builder
     */
    private static void addReferenceMaterialSpecificFields
                                            (ReferenceMaterial refMaterial,
                                             ResourceSummaryResponse.ResourceSummaryResponseBuilder builder) {
        builder.author(refMaterial.getAuthor())
               .publisher(refMaterial.getPublisher())
               .format(refMaterial.getFormat())
               .coverImageUrl(refMaterial.getCoverImageUrl());
    }
    
    // ==================== Quiz Mappings ====================
    
    /**
     * Maps a Quiz entity to a detailed QuizResponse DTO
     */
    public static QuizResponse toQuizResponse(Quiz quiz) {
        Course course = quiz.getCourse();
        
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .courseId(course != null ? course.getId() : null)
                .courseCode(course != null ? course.getCourseCode() : null)
                .courseName(course != null ? course.getCourseName() : null)
                .numberOfQuestions(quiz.getNumberOfQuestions())
                .difficultyLevel(quiz.getDifficultyLevel())
                .quizType(quiz.getQuizType())
                .yearGiven(quiz.getYearGiven())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }
    
    /**
     * Maps a CreateQuizRequest DTO to a Quiz entity
     */
    public static Quiz toQuizEntity(CreateQuizRequest request, Course course) {
        return Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .course(course)
                .numberOfQuestions(request.getNumberOfQuestions())
                .difficultyLevel(request.getDifficultyLevel())
                .quizType(request.getQuizType())
                .yearGiven(request.getYearGiven())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Updates a Quiz entity from an UpdateQuizRequest DTO
     */
    public static void updateQuizFromRequest(Quiz quiz, UpdateQuizRequest request, Course course) {
        if (request.getTitle() != null) {
            quiz.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            quiz.setDescription(request.getDescription());
        }
        if (course != null) {
            quiz.setCourse(course);
        }
        if (request.getNumberOfQuestions() == 0) {
            quiz.setNumberOfQuestions(request.getNumberOfQuestions());
        }
        if (request.getDifficultyLevel() != null) {
            quiz.setDifficultyLevel(request.getDifficultyLevel());
        }
        if (request.getQuizType() != null) {
            quiz.setQuizType(request.getQuizType());
        }

        if (request.getYearGiven() < 1900 || request.getYearGiven() > 2100) {
            quiz.setYearGiven(request.getYearGiven());
        }
        quiz.setUpdatedAt(LocalDateTime.now());
    }
    
    // ==================== Reference Material Mappings ====================
    
    /**
     * Maps a ReferenceMaterial entity to a detailed ReferenceMaterialResponse DTO
     */
    public static ReferenceMaterialResponse toReferenceMaterialResponse(ReferenceMaterial material) {
        Course course = material.getCourse();
        
        return ReferenceMaterialResponse.builder()
                .id(material.getId())
                .title(material.getTitle())
                .description(material.getDescription())
                .courseId(course != null ? course.getId() : null)
                .courseCode(course != null ? course.getCourseCode() : null)
                .courseName(course != null ? course.getCourseName() : null)
                .author(material.getAuthor())
                .publisher(material.getPublisher())
                .isbn(material.getIsbn())
                .edition(material.getEdition())
                .publicationYear(material.getPublicationYear())
                .language(material.getLanguage())
                .numberOfPages(material.getNumberOfPages())
                .format(material.getFormat())
                .fileSize(material.getFileSize())
                .fileUrl(material.getFileUrl())
                .downloadLink(material.getDownloadLink())
                .coverImageUrl(material.getCoverImageUrl())
                .createdAt(material.getCreatedAt())
                .updatedAt(material.getUpdatedAt())
                .build();
    }
    
    /**
     * Maps a CreateReferenceMaterialRequest DTO to a ReferenceMaterial entity
     */
    public static ReferenceMaterial toReferenceMaterialEntity(CreateReferenceMaterialRequest request, Course course) {
        return ReferenceMaterial.builder()
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
    }
    
    /**
     * Updates a ReferenceMaterial entity from an UpdateReferenceMaterialRequest DTO
     */
    public static void updateReferenceMaterialFromRequest(ReferenceMaterial material, UpdateReferenceMaterialRequest request, Course course) {
        if (request.getTitle() != null) {
            material.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            material.setDescription(request.getDescription());
        }
        if (course != null) {
            material.setCourse(course);
        }
        if (request.getAuthor() != null) {
            material.setAuthor(request.getAuthor());
        }
        if (request.getPublisher() != null) {
            material.setPublisher(request.getPublisher());
        }
        if (request.getIsbn() != null) {
            material.setIsbn(request.getIsbn());
        }
        if (request.getEdition() != null) {
            material.setEdition(request.getEdition());
        }
        if (request.getPublicationYear() != null) {
            material.setPublicationYear(request.getPublicationYear());
        }
        if (request.getLanguage() != null) {
            material.setLanguage(request.getLanguage());
        }
        if (request.getNumberOfPages() != null) {
            material.setNumberOfPages(request.getNumberOfPages());
        }
        if (request.getFormat() != null) {
            material.setFormat(request.getFormat());
        }
        if (request.getFileSize() != null) {
            material.setFileSize(request.getFileSize());
        }
        if (request.getFileUrl() != null) {
            material.setFileUrl(request.getFileUrl());
        }
        if (request.getDownloadLink() != null) {
            material.setDownloadLink(request.getDownloadLink());
        }
        if (request.getCoverImageUrl() != null) {
            material.setCoverImageUrl(request.getCoverImageUrl());
        }
        material.setUpdatedAt(LocalDateTime.now());
    }
    
    // ==================== Course Mappings ====================
    
    /**
     * Maps a Course entity to a CourseResponse DTO
     */
    public static CourseResponse toCourseResponse(Course course) {
        Department department = course.getDepartment();
        Faculty faculty = department != null ? department.getFaculty() : null;
        College college = faculty != null ? faculty.getCollege() : null;
        
        List<ProgramMinimalResponse> programs = course.getPrograms() != null ?
                course.getPrograms().stream()
                        .map(EntityMapper::toProgramMinimalResponse)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
        
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .courseDescription(course.getCourseDescription())
                .semester(course.getSemester())
                .departmentId(department != null ? department.getId() : null)
                .departmentName(department != null ? department.getDepartmentName() : null)
                .facultyId(faculty != null ? faculty.getId() : null)
                .facultyName(faculty != null ? faculty.getFacultyName() : null)
                .collegeId(college != null ? college.getId() : null)
                .collegeName(college != null ? college.getCollegeName() : null)
                .programs(programs)
                .build();
    }
    
    /**
     * Maps a Course entity to a minimal CourseMinimalResponse DTO
     */
    public static CourseMinimalResponse toCourseMinimalResponse(Course course) {
        return CourseMinimalResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .build();
    }
    
    /**
     * Maps a CreateCourseRequest DTO to a Course entity
     */
    public static Course toCourseEntity(CreateCourseRequest request, Department department, Set<Program> programs) {
        return Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .courseDescription(request.getCourseDescription())
                .semester(request.getSemester())
                .department(department)
                .programs(programs != null ? programs : Collections.emptySet())
                .build();
    }
    
    // ==================== Program Mappings ====================
    
    /**
     * Maps a Program entity to a ProgramResponse DTO
     */
    public static ProgramResponse toProgramResponse(Program program) {
        Department department = program.getDepartment();
        Faculty faculty = department != null ? department.getFaculty() : null;
        College college = faculty != null ? faculty.getCollege() : null;
        
        List<CourseMinimalResponse> courses = program.getCourses() != null ?
                program.getCourses().stream()
                        .map(EntityMapper::toCourseMinimalResponse)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
        
        return ProgramResponse.builder()
                .id(program.getId())
                .programName(program.getProgramName())
                .programCode(program.getProgramCode())
                .departmentId(department != null ? department.getId() : null)
                .departmentName(department != null ? department.getDepartmentName() : null)
                .facultyId(faculty != null ? faculty.getId() : null)
                .facultyName(faculty != null ? faculty.getFacultyName() : null)
                .collegeId(college != null ? college.getId() : null)
                .collegeName(college != null ? college.getCollegeName() : null)
                .courses(courses)
                .build();
    }

    /**
     * Maps a Program entity to a minimal ProgramMinimalResponse DTO
     */
    public static ProgramMinimalResponse toProgramMinimalResponse(Program program) {
        if (program == null) {
            return null;
        }

        return ProgramMinimalResponse.builder()
                .id(program.getId())
                .programName(program.getProgramName())
                .programCode(program.getProgramCode())
                .build();
    }

    /**
     * Maps a CreateProgramRequest DTO to a Program entity
     */
    public static Program toProgramEntity(CreateProgramRequest request, Department department, Set<Course> courses) {
        if (request == null) {
            return null;
        }

        return Program.builder()
                .programName(request.getProgramName())
                .programCode(request.getProgramCode())
                .department(department)
                .courses(courses != null ? courses : Collections.emptySet())
                .build();
    }

    /**
     * Updates a Program entity from an UpdateProgramRequest DTO
     */
    public static void updateProgramFromRequest(Program program, UpdateProgramRequest request, Department department, Set<Course> courses) {
        if (program == null || request == null) {
            return;
        }

        if (request.getProgramName() != null) {
            program.setProgramName(request.getProgramName());
        }
        if (request.getProgramCode() != null) {
            program.setProgramCode(request.getProgramCode());
        }
        if (department != null) {
            program.setDepartment(department);
        }
        if (courses != null) {
            program.setCourses(courses);
        }
    }

    // ==================== Department Mappings ====================

    /**
     * Maps a Department entity to a DepartmentResponse DTO
     */
    public static DepartmentResponse toDepartmentResponse(Department department) {
        if (department == null) {
            return null;
        }

        Faculty faculty = department.getFaculty();
        College college = faculty != null ? faculty.getCollege() : null;

        return DepartmentResponse.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .facultyId(faculty != null ? faculty.getId() : null)
                .facultyName(faculty != null ? faculty.getFacultyName() : null)
                .collegeId(college != null ? college.getId() : null)
                .collegeName(college != null ? college.getCollegeName() : null)
                .build();
    }

    /**
     * Maps a CreateDepartmentRequest DTO to a Department entity
     */
    public static Department toDepartmentEntity(CreateDepartmentRequest request, Faculty faculty) {
        if (request == null) {
            return null;
        }

        return Department.builder()
                .departmentName(request.getDepartmentName())
                .faculty(faculty)
                .build();
    }

    /**
     * Updates a Department entity from an UpdateDepartmentRequest DTO
     */
    public static void updateDepartmentFromRequest(Department department, UpdateDepartmentRequest request, Faculty faculty) {
        if (department == null || request == null) {
            return;
        }

        if (request.getDepartmentName() != null) {
            department.setDepartmentName(request.getDepartmentName());
        }
        if (faculty != null) {
            department.setFaculty(faculty);
        }
    }

    // ==================== Faculty Mappings ====================

    /**
     * Maps a Faculty entity to a FacultyResponse DTO
     */
    public static FacultyResponse toFacultyResponse(Faculty faculty) {
        if (faculty == null) {
            return null;
        }

        College college = faculty.getCollege();

        return FacultyResponse.builder()
                .id(faculty.getId())
                .facultyName(faculty.getFacultyName())
                .collegeId(college != null ? college.getId() : null)
                .collegeName(college != null ? college.getCollegeName() : null)
                .build();
    }

    /**
     * Maps a CreateFacultyRequest DTO to a Faculty entity
     */
    public static Faculty toFacultyEntity(CreateFacultyRequest request, College college) {
        if (request == null) {
            return null;
        }

        return Faculty.builder()
                .facultyName(request.getFacultyName())
                .college(college)
                .build();
    }

    /**
     * Updates a Faculty entity from an UpdateFacultyRequest DTO
     */
    public static void updateFacultyFromRequest(Faculty faculty, UpdateFacultyRequest request, College college) {
        if (faculty == null || request == null) {
            return;
        }

        if (request.getFacultyName() != null) {
            faculty.setFacultyName(request.getFacultyName());
        }
        if (college != null) {
            faculty.setCollege(college);
        }
    }

    // ==================== College Mappings ====================

    /**
     * Maps a College entity to a CollegeResponse DTO
     */
    public static CollegeResponse toCollegeResponse(College college) {
        if (college == null) {
            return null;
        }

        return CollegeResponse.builder()
                .id(college.getId())
                .collegeName(college.getCollegeName())
                .build();
    }

    /**
     * Maps a CreateCollegeRequest DTO to a College entity
     */
    public static College toCollegeEntity(CreateCollegeRequest request) {
        if (request == null) {
            return null;
        }

        return College.builder()
                .collegeName(request.getCollegeName())
                .build();
    }

    /**
     * Updates a College entity from an UpdateCollegeRequest DTO
     */
    public static void updateCollegeFromRequest(College college, UpdateCollegeRequest request) {
        if (college == null || request == null) {
            return;
        }

        if (request.getCollegeName() != null) {
            college.setCollegeName(request.getCollegeName());
        }
    }
}
