package com.merbsconnect.academics.util;

import com.merbsconnect.academics.domain.*;
import com.merbsconnect.academics.dto.response.*;
import com.merbsconnect.academics.dto.request.*;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.dto.request.RegistrationRequest;
import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.security.CustomUserDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between domain entities and DTOs.
 * This class provides static methods for converting objects across the application.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceMapper {


    // ==================== Resource Mappings ====================

    /**
     * Converts a Resource entity to a ResourceSummaryResponse DTO
     */
    public static ResourceSummaryResponse toResourceSummaryResponse(Resource resource) {
        ResourceSummaryResponse.ResourceSummaryResponseBuilder builder = ResourceSummaryResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .resourceType(resource.getClass().getSimpleName())
                .courseId(resource.getCourse().getId())
                .courseCode(resource.getCourse().getCourseCode())
                .courseName(resource.getCourse().getCourseName())
                .createdAt(resource.getCreatedAt());

        // Add specific fields based on resource type
        if (resource instanceof Quiz) {
            Quiz quiz = (Quiz) resource;
            builder.difficultyLevel(quiz.getDifficultyLevel())
                    .yearGiven(quiz.getYearGiven())
                    .quizType(quiz.getQuizType() != null ? quiz.getQuizType().name() : null);
        } else if (resource instanceof ReferenceMaterial) {
            ReferenceMaterial refMaterial = (ReferenceMaterial) resource;
            builder.author(refMaterial.getAuthor())
                    .publisher(refMaterial.getPublisher())
                    .coverImageUrl(refMaterial.getCoverImageUrl());
        }

        return builder.build();
    }

    /**
     * Converts a Page of Resource entities to a ResourceSearchResponse DTO
     */
    public static <T extends Resource> ResourceSearchResponse<ResourceSummaryResponse> toResourceSearchResponse(Page<T> resourcePage) {
        List<ResourceSummaryResponse> content = resourcePage.getContent().stream()
                .map(ResourceMapper::toResourceSummaryResponse)
                .collect(Collectors.toList());

        return ResourceSearchResponse.<ResourceSummaryResponse>builder()
                .content(content)
                .page(resourcePage.getNumber())
                .size(resourcePage.getSize())
                .totalElements(resourcePage.getTotalElements())
                .totalPages(resourcePage.getTotalPages())
                .last(resourcePage.isLast())
                .build();
    }

    // ==================== Quiz Mappings ====================

    /**
     * Converts a CreateQuizRequest DTO to a Quiz entity
     */
    public static Quiz toQuiz(CreateQuizRequest request, Course course) {
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
     * Converts a Question entity to a QuestionResponse DTO
     */
    public static QuestionResponse toQuestionResponse(Question question) {
        ResourceMinimalResponse referencedResource = null;
        if (question.getReferencedResource() != null) {
            Resource resource = question.getReferencedResource();
            referencedResource = ResourceMinimalResponse.builder()
                    .id(resource.getId())
                    .title(resource.getTitle())
                    .resourceType(resource.getClass().getSimpleName())
                    .build();
        }

        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .possibleAnswers(question.getPossibleAnswers())
                .correctAnswer(question.getCorrectAnswer())
                .explanationSteps(question.getExplanationSteps())
                .referencedResource(referencedResource)
                .build();
    }

    /**
     * Converts a Quiz entity to a detailed response DTO
     */
    public static QuizResponse toQuizResponse(Quiz quiz) {
        Course course = quiz.getCourse();

        // Map questions if available
        List<QuestionResponse> questionResponses = null;
        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
            questionResponses = quiz.getQuestions().stream()
                    .map(ResourceMapper::toQuestionResponse)
                    .collect(Collectors.toList());
        }

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
//                .timeLimit(quiz.getTimeLimit())
//                .passingScore(quiz.getPassingScore())
                .yearGiven(quiz.getYearGiven())
                .questions(questionResponses)
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }

    /**
     * Converts a ReferenceMaterial request to a ReferenceMaterial entity
     */
    public static ReferenceMaterial toReferenceMaterial(CreateReferenceMaterialRequest request, Course course) {
        return ReferenceMaterial.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .course(course)
                .fileUrl(request.getFileUrl())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .isbn(request.getIsbn())
                .edition(request.getEdition())
                .publicationYear(request.getPublicationYear())
                .language(request.getLanguage())
                .numberOfPages(request.getNumberOfPages())
                .format(request.getFormat())
                .fileSize(request.getFileSize())
                .downloadLink(request.getDownloadLink())
                .coverImageUrl(request.getCoverImageUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== Course Mappings ====================

    /**
     * Converts a Course entity to a CourseResponse DTO
     */
    public static CourseResponse toCourseResponse(Course course) {
        Department department = course.getDepartment();
        Faculty faculty = department != null ? department.getFaculty() : null;
        College college = faculty != null ? faculty.getCollege() : null;

        List<ProgramMinimalResponse> programs = course.getPrograms().stream()
                .map(ResourceMapper::toProgramMinimalResponse)
                .collect(Collectors.toList());

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
     * Converts a Course entity to a minimal response DTO
     */
    public static CourseMinimalResponse toCourseMinimalResponse(Course course) {
        return CourseMinimalResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .build();
    }

    /**
     * Converts a CreateCourseRequest DTO to a Course entity
     */
    public static Course toCourse(CreateCourseRequest request, Department department, Set<Program> programs) {
        return Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .courseDescription(request.getCourseDescription())
                .semester(request.getSemester())
                .department(department)
                .programs(programs)
                .build();
    }

    /**
     * Updates a Course entity from an UpdateCourseRequest DTO
     */
    public static void updateCourseFromRequest(Course course, UpdateCourseRequest request, Department department, Set<Program> programs) {
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
        if (department != null) {
            course.setDepartment(department);
        }
        if (programs != null) {
            course.setPrograms(programs);
        }
    }

    // ==================== Program Mappings ====================

    /**
     * Converts a Program entity to a ProgramResponse DTO
     */
    public static ProgramResponse toProgramResponse(Program program) {
        Department department = program.getDepartment();
        Faculty faculty = department != null ? department.getFaculty() : null;
        College college = faculty != null ? faculty.getCollege() : null;

        List<CourseMinimalResponse> courses = program.getCourses().stream()
                .map(ResourceMapper::toCourseMinimalResponse)
                .collect(Collectors.toList());

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
     * Converts a Program entity to a minimal response DTO
     */
    public static ProgramMinimalResponse toProgramMinimalResponse(Program program) {
        return ProgramMinimalResponse.builder()
                .id(program.getId())
                .programName(program.getProgramName())
                .programCode(program.getProgramCode())
                .build();
    }

    /**
     * Converts a CreateProgramRequest DTO to a Program entity
     */
    public static Program toProgram(CreateProgramRequest request, Department department, Set<Course> courses) {
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
     * Converts a Department entity to a DepartmentResponse DTO
     */
    public static DepartmentResponse toDepartmentResponse(Department department) {
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
     * Converts a CreateDepartmentRequest DTO to a Department entity
     */
    public static Department toDepartment(CreateDepartmentRequest request, Faculty faculty) {
        return Department.builder()
                .departmentName(request.getDepartmentName())
                .faculty(faculty)
                .build();
    }

    // ==================== Faculty Mappings ====================

    /**
     * Converts a Faculty entity to a FacultyResponse DTO
     */
    public static FacultyResponse toFacultyResponse(Faculty faculty) {
        College college = faculty.getCollege();

        return FacultyResponse.builder()
                .id(faculty.getId())
                .facultyName(faculty.getFacultyName())
                .collegeId(college != null ? college.getId() : null)
                .collegeName(college != null ? college.getCollegeName() : null)
                .build();
    }

    /**
     * Converts a CreateFacultyRequest DTO to a Faculty entity
     */
    public static Faculty toFaculty(CreateFacultyRequest request, College college) {
        return Faculty.builder()
                .facultyName(request.getFacultyName())
                .college(college)
                .build();
    }

    // ==================== College Mappings ====================

    /**
     * Converts a College entity to a CollegeResponse DTO
     */
    public static CollegeResponse toCollegeResponse(College college) {
        return CollegeResponse.builder()
                .id(college.getId())
                .collegeName(college.getCollegeName())
                .build();
    }

    /**
     * Converts a CreateCollegeRequest DTO to a College entity
     */
    public static College toCollege(CreateCollegeRequest request) {
        return College.builder()
                .collegeName(request.getCollegeName())
                .build();
    }

    // ==================== Authentication Mappings ====================

    /**
     * Converts a RegistrationRequest DTO to a User entity
     */
    public static User toUser(RegistrationRequest request, String encodedPassword) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(encodedPassword)
                .isEnabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a JwtResponse from user details and tokens
     */
    public static JwtResponse toJwtResponse(CustomUserDetails userDetails, String jwt, String refreshToken) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .roles(roles)
                .build();
    }


    /**
     * Converts a ReferenceMaterial entity to a detailed response DTO
     */
    public static ReferenceMaterialResponse toReferenceMaterialResponse(ReferenceMaterial referenceMaterial) {
        Course course = referenceMaterial.getCourse();

        return ReferenceMaterialResponse.builder()
                .id(referenceMaterial.getId())
                .title(referenceMaterial.getTitle())
                .description(referenceMaterial.getDescription())
                .courseId(course != null ? course.getId() : null)
                .courseCode(course != null ? course.getCourseCode() : null)
                .courseName(course != null ? course.getCourseName() : null)
                .fileUrl(referenceMaterial.getFileUrl())
                .author(referenceMaterial.getAuthor())
                .publisher(referenceMaterial.getPublisher())
                .isbn(referenceMaterial.getIsbn())
                .edition(referenceMaterial.getEdition())
                .publicationYear(referenceMaterial.getPublicationYear())
                .language(referenceMaterial.getLanguage())
                .numberOfPages(referenceMaterial.getNumberOfPages())
                .format(referenceMaterial.getFormat())
                .fileSize(referenceMaterial.getFileSize())
                .downloadLink(referenceMaterial.getDownloadLink())
                .coverImageUrl(referenceMaterial.getCoverImageUrl())
                .createdAt(referenceMaterial.getCreatedAt())
                .updatedAt(referenceMaterial.getUpdatedAt())
                .build();
    }
}