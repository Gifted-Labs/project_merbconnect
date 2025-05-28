package com.merbsconnect.academics.controller;

import com.merbsconnect.academics.dto.request.AddQuestionToQuizRequest;
import com.merbsconnect.academics.dto.request.CreateQuizRequest;
import com.merbsconnect.academics.dto.request.UpdateQuestionRequest;
import com.merbsconnect.academics.dto.request.UpdateQuizRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.QuestionResponse;
import com.merbsconnect.academics.dto.response.QuizResponse;
import com.merbsconnect.academics.service.QuizService;
import com.merbsconnect.enums.QuizType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Quiz entities.
 */
@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;

    /**
     * Creates a new quiz.
     *
     * @param request the quiz creation request
     * @return the created quiz response wrapped in ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        log.info("REST request to create Quiz: {}", request);
        ApiResponse<QuizResponse> response = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Quiz created successfully", response.getData()));
    }

    /**
     * Updates an existing quiz.
     *
     * @param id the quiz ID
     * @param request the quiz update request
     * @return the updated quiz response wrapped in ApiResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuizRequest request) {
        log.info("REST request to update Quiz with ID: {}", id);
        ApiResponse<QuizResponse> response = quizService.updateQuiz(id, request);
        return ResponseEntity.ok(ApiResponse.success("Quiz updated successfully", response.getData()));
    }

    /**
     * Retrieves a quiz by its ID.
     *
     * @param id the quiz ID
     * @return the quiz response wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizById(@PathVariable Long id) {
        log.info("REST request to get Quiz with ID: {}", id);
        ApiResponse<QuizResponse> response = quizService.getQuizById(id);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Deletes a quiz by its ID.
     *
     * @param id the quiz ID
     * @return API response indicating success or failure
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long id) {
        log.info("REST request to delete Quiz with ID: {}", id);
        ApiResponse<Void> response = quizService.deleteQuiz(id);
        return ResponseEntity.ok(ApiResponse.success("Quiz deleted successfully", null));
    }

    /**
     * Retrieves all quizzes with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of quiz responses wrapped in ApiResponse
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<QuizResponse>>> getAllQuizzesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all Quizzes with pagination - page: {}, size: {}", page, size);
        ApiResponse<PageResponse<QuizResponse>> response = quizService.getAllQuizzes(page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves quizzes by quiz type with pagination.
     *
     * @param quizType the quiz type
     * @param page the page number
     * @param size the page size
     * @return paginated list of quiz responses wrapped in ApiResponse
     */
    @GetMapping("/by-quiz-type/{quizType}")
    public ResponseEntity<ApiResponse<PageResponse<QuizResponse>>> getQuizzesByQuizType(
            @PathVariable QuizType quizType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Quizzes by quiz type: {} with pagination - page: {}, size: {}", 
                quizType, page, size);
        ApiResponse<PageResponse<QuizResponse>> response = quizService.getQuizzesByQuizType(quizType, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves quizzes by year given with pagination.
     *
     * @param yearGiven the year given
     * @param page the page number
     * @param size the page size
     * @return paginated list of quiz responses wrapped in ApiResponse
     */
    @GetMapping("/by-year/{yearGiven}")
    public ResponseEntity<ApiResponse<PageResponse<QuizResponse>>> getQuizzesByYearGiven(
            @PathVariable int yearGiven,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Quizzes by year given: {} with pagination - page: {}, size: {}", 
                yearGiven, page, size);
        ApiResponse<PageResponse<QuizResponse>> response = quizService.getQuizzesByYearGiven(yearGiven, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves quizzes by quiz type and year given with pagination.
     *
     * @param quizType the quiz type
     * @param yearGiven the year given
     * @param page the page number
     * @param size the page size
     * @return paginated list of quiz responses wrapped in ApiResponse
     */
    @GetMapping("/by-quiz-type/{quizType}/by-year/{yearGiven}")
    public ResponseEntity<ApiResponse<PageResponse<QuizResponse>>> getQuizzesByQuizTypeAndYearGiven(
            @PathVariable QuizType quizType,
            @PathVariable int yearGiven,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Quizzes by quiz type: {} and year given: {} with pagination - page: {}, size: {}", 
                quizType, yearGiven, page, size);
        ApiResponse<PageResponse<QuizResponse>> response = 
                quizService.getQuizzesByQuizTypeAndYearGiven(quizType, yearGiven, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves quizzes by course ID and quiz type with pagination.
     *
     * @param courseId the course ID
     * @param quizType the quiz type
     * @param page the page number
     * @param size the page size
     * @return paginated list of quiz responses wrapped in ApiResponse
     */
    @GetMapping("/by-course/{courseId}/by-quiz-type/{quizType}")
    public ResponseEntity<ApiResponse<PageResponse<QuizResponse>>> getQuizzesByCourseIdAndQuizType(
            @PathVariable Long courseId,
            @PathVariable QuizType quizType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Quizzes by course ID: {} and quiz type: {} with pagination - page: {}, size: {}", 
                courseId, quizType, page, size);
        ApiResponse<PageResponse<QuizResponse>> response = 
                quizService.getQuizzesByCourseIdAndQuizType(courseId, quizType, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves quizzes by course ID and year given with pagination.
     *
     * @param courseId the course ID
     * @param yearGiven the year given
     * @param page the page number
     * @param size the page size
     * @return paginated list of quiz responses wrapped in ApiResponse
     */
    @GetMapping("/by-course/{courseId}/by-year/{yearGiven}")
    public ResponseEntity<ApiResponse<PageResponse<QuizResponse>>> getQuizzesByCourseIdAndYearGiven(
            @PathVariable Long courseId,
            @PathVariable int yearGiven,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Quizzes by course ID: {} and year given: {} with pagination - page: {}, size: {}", 
                courseId, yearGiven, page, size);
        ApiResponse<PageResponse<QuizResponse>> response = 
                quizService.getQuizzesByCourseIdAndYearGiven(courseId, yearGiven, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves quizzes by course ID, quiz type, and year given with pagination.
     *
     * @param courseId the course ID
     * @param quizType the quiz type
     * @param yearGiven the year given
     * @param page the page number
     * @param size the page size
     * @return paginated list of quiz responses wrapped in ApiResponse
     */
    @GetMapping("/by-course/{courseId}/by-quiz-type/{quizType}/by-year/{yearGiven}")
    public ResponseEntity<ApiResponse<PageResponse<QuizResponse>>> getQuizzesByCourseIdAndQuizTypeAndYearGiven(
            @PathVariable Long courseId,
            @PathVariable QuizType quizType,
            @PathVariable int yearGiven,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Quizzes by course ID: {}, quiz type: {}, and year given: {} with pagination - page: {}, size: {}", 
                courseId, quizType, yearGiven, page, size);
        ApiResponse<PageResponse<QuizResponse>> response = 
                quizService.getQuizzesByCourseIdAndQuizTypeAndYearGiven(courseId, quizType, yearGiven, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves quizzes by difficulty level with pagination.
     *
     * @param difficultyLevel the difficulty level
     * @param page the page number
     * @param size the page size
     * @return paginated list of quiz responses wrapped in ApiResponse
     */
    @GetMapping("/by-difficulty/{difficultyLevel}")
    public ResponseEntity<ApiResponse<PageResponse<QuizResponse>>> getQuizzesByDifficultyLevel(
            @PathVariable String difficultyLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get Quizzes by difficulty level: {} with pagination - page: {}, size: {}", 
                difficultyLevel, page, size);
        ApiResponse<PageResponse<QuizResponse>> response = 
                quizService.getQuizzesByDifficultyLevel(difficultyLevel, page, size);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Adds questions to a quiz.
     *
     * @param quizId the quiz ID
     * @param request the request containing questions to add
     * @return the updated quiz response wrapped in ApiResponse
     */
    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponse>> addQuestionsToQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody AddQuestionToQuizRequest request) {
        log.info("REST request to add questions to Quiz with ID: {}", quizId);
        ApiResponse<QuizResponse> response = quizService.addQuestionsToQuiz(quizId, request);
        return ResponseEntity.ok(ApiResponse.success("Questions added to quiz successfully", response.getData()));
    }

    /**
     * Updates a question in a quiz.
     *
     * @param quizId the quiz ID
     * @param questionId the question ID
     * @param request the question update request
     * @return the updated question response wrapped in ApiResponse
     */
    @PutMapping("/{quizId}/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestionInQuiz(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @Valid @RequestBody UpdateQuestionRequest request) {
        log.info("REST request to update Question with ID: {} in Quiz with ID: {}", questionId, quizId);
        ApiResponse<QuestionResponse> response = quizService.updateQuestionInQuiz(quizId, questionId, request);
        return ResponseEntity.ok(ApiResponse.success("Question updated successfully", response.getData()));
    }

    /**
     * Removes a question from a quiz.
     *
     * @param quizId the quiz ID
     * @param questionId the question ID
     * @return API response indicating success or failure
     */
    @DeleteMapping("/{quizId}/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeQuestionFromQuiz(
            @PathVariable Long quizId,
            @PathVariable Long questionId) {
        log.info("REST request to remove Question with ID: {} from Quiz with ID: {}", questionId, quizId);
        ApiResponse<Void> response = quizService.removeQuestionFromQuiz(quizId, questionId);
        return ResponseEntity.ok(ApiResponse.success("Question removed from quiz successfully", null));
    }

    /**
     * Retrieves all distinct years given for quizzes.
     *
     * @return list of distinct years wrapped in ApiResponse
     */
    @GetMapping("/years")
    public ResponseEntity<ApiResponse<List<Integer>>> getDistinctYearGiven() {
        log.info("REST request to get all distinct years given for quizzes");
        ApiResponse<List<Integer>> response = quizService.getDistinctYearGiven();
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves distinct years given for quizzes by quiz type.
     *
     * @param quizType the quiz type
     * @return list of distinct years wrapped in ApiResponse
     */
    @GetMapping("/by-quiz-type/{quizType}/years")
    public ResponseEntity<ApiResponse<List<Integer>>> getDistinctYearGivenByQuizType(
            @PathVariable QuizType quizType) {
        log.info("REST request to get distinct years given for quizzes by quiz type: {}", quizType);
        ApiResponse<List<Integer>> response = quizService.getDistinctYearGivenByQuizType(quizType);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }

    /**
     * Retrieves distinct years given for quizzes by course ID.
     *
     * @param courseId the course ID
     * @return list of distinct years wrapped in ApiResponse
     */
    @GetMapping("/by-course/{courseId}/years")
    public ResponseEntity<ApiResponse<List<Integer>>> getDistinctYearGivenByCourseId(
            @PathVariable Long courseId) {
        log.info("REST request to get distinct years given for quizzes by course ID: {}", courseId);
        ApiResponse<List<Integer>> response = quizService.getDistinctYearGivenByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(response.getData()));
    }
}