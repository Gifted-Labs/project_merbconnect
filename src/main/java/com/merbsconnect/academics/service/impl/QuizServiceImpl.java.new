package com.merbsconnect.academics.service.impl;

import com.merbsconnect.academics.domain.Course;
import com.merbsconnect.academics.domain.Question;
import com.merbsconnect.academics.domain.Quiz;
import com.merbsconnect.academics.domain.Resource;
import com.merbsconnect.academics.dto.request.AddQuestionToQuizRequest;
import com.merbsconnect.academics.dto.request.CreateQuestionRequest;
import com.merbsconnect.academics.dto.request.CreateQuizRequest;
import com.merbsconnect.academics.dto.request.UpdateQuestionRequest;
import com.merbsconnect.academics.dto.request.UpdateQuizRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.QuestionResponse;
import com.merbsconnect.academics.dto.response.QuizResponse;
import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.academics.repository.CourseRepository;
import com.merbsconnect.academics.repository.QuizRepository;
import com.merbsconnect.academics.repository.ResourceRepository;
import com.merbsconnect.academics.service.QuizService;
import com.merbsconnect.academics.util.ResourceMapper;
import com.merbsconnect.enums.QuizType;
import com.merbsconnect.util.mapper.PaginationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the QuizService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public ApiResponse<QuizResponse> createQuiz(CreateQuizRequest request) {
        log.info("Creating new quiz with title: {}", request.getTitle());

        // Get course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> {
                    log.error("Course not found with ID: {}", request.getCourseId());
                    return new ResourceNotFoundException("Course not found with ID: " + request.getCourseId());
                });

        // Create quiz
        Quiz quiz = ResourceMapper.toQuiz(request, course);

        // Add questions
        List<Question> questions = new ArrayList<>();
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            questions = request.getQuestions().stream()
                    .map(this::createQuestionFromRequest)
                    .collect(Collectors.toList());
        }
        quiz.setQuestions(questions);

        // Save quiz
        Quiz savedQuiz = quizRepository.save(quiz);

        log.info("Quiz created successfully with ID: {}", savedQuiz.getId());
        return ApiResponse.success("Quiz created successfully", ResourceMapper.toQuizResponse(savedQuiz));
    }

    @Override
    public ApiResponse<QuizResponse> updateQuiz(Long id, UpdateQuizRequest request) {
        log.info("Updating quiz with ID: {}", id);

        // Find quiz
        Quiz quiz = findQuizById(id);

        // Update course if provided
        if (request.getCourseId() != null && !request.getCourseId().equals(quiz.getCourse().getId())) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> {
                        log.error("Course not found with ID: {}", request.getCourseId());
                        return new ResourceNotFoundException("Course not found with ID: " + request.getCourseId());
                    });
            quiz.setCourse(course);
        }

        // Update other fields
        if (request.getTitle() != null) {
            quiz.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            quiz.setDescription(request.getDescription());
        }
        if (request.getNumberOfQuestions() > 0) {
            quiz.setNumberOfQuestions(request.getNumberOfQuestions());
        }
        if (request.getDifficultyLevel() != null) {
            quiz.setDifficultyLevel(request.getDifficultyLevel());
        }
        if (request.getYearGiven() > 0) {
            quiz.setYearGiven(request.getYearGiven());
        }
        if (request.getQuizType() != null) {
            quiz.setQuizType(request.getQuizType());
        }

        // Save updated quiz
        Quiz updatedQuiz = quizRepository.save(quiz);

        log.info("Quiz updated successfully with ID: {}", updatedQuiz.getId());
        return ApiResponse.success("Quiz updated successfully", ResourceMapper.toQuizResponse(updatedQuiz));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<QuizResponse> getQuizById(Long id) {
        log.info("Retrieving quiz with ID: {}", id);
        Quiz quiz = findQuizById(id);
        return ApiResponse.success(ResourceMapper.toQuizResponse(quiz));
    }

    @Override
    public ApiResponse<Void> deleteQuiz(Long id) {
        log.info("Deleting quiz with ID: {}", id);

        // Check if quiz exists
        if (!quizRepository.existsById(id)) {
            log.error("Quiz not found with ID: {}", id);
            throw new ResourceNotFoundException("Quiz not found with ID: " + id);
        }

        quizRepository.deleteById(id);

        log.info("Quiz deleted successfully with ID: {}", id);
        return ApiResponse.success("Quiz deleted successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<QuizResponse>> getAllQuizzes(int page, int size) {
        log.info("Retrieving all quizzes with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quiz> quizPage = quizRepository.findAll(pageable);

        PageResponse<QuizResponse> pageResponse = PaginationMapper.mapToPageResponse(
                quizPage, ResourceMapper::toQuizResponse);

        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<QuizResponse>> getQuizzesByQuizType(QuizType quizType, int page, int size) {
        log.info("Retrieving quizzes by quiz type: {} with pagination - page: {}, size: {}", quizType, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quiz> quizPage = quizRepository.findByQuizType(quizType, pageable);

        PageResponse<QuizResponse> pageResponse = PaginationMapper.mapToPageResponse(
                quizPage, ResourceMapper::toQuizResponse);

        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<QuizResponse>> getQuizzesByYearGiven(int yearGiven, int page, int size) {
        log.info("Retrieving quizzes by year given: {} with pagination - page: {}, size: {}", yearGiven, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quiz> quizPage = quizRepository.findByYearGiven(yearGiven, pageable);

        PageResponse<QuizResponse> pageResponse = PaginationMapper.mapToPageResponse(
                quizPage, ResourceMapper::toQuizResponse);

        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<QuizResponse>> getQuizzesByQuizTypeAndYearGiven(
            QuizType quizType, int yearGiven, int page, int size) {
        log.info("Retrieving quizzes by quiz type: {} and year given: {} with pagination - page: {}, size: {}",
                quizType, yearGiven, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quiz> quizPage = quizRepository.findByQuizTypeAndYearGiven(quizType, yearGiven, pageable);

        PageResponse<QuizResponse> pageResponse = PaginationMapper.mapToPageResponse(
                quizPage, ResourceMapper::toQuizResponse);

        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<QuizResponse>> getQuizzesByCourseIdAndQuizType(
            Long courseId, QuizType quizType, int page, int size) {
        log.info("Retrieving quizzes by course ID: {} and quiz type: {} with pagination - page: {}, size: {}",
                courseId, quizType, page, size);

        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            log.error("Course not found with ID: {}", courseId);
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quiz> quizPage = quizRepository.findByCourseIdAndQuizType(courseId, quizType, pageable);

        PageResponse<QuizResponse> pageResponse = PaginationMapper.mapToPageResponse(
                quizPage, ResourceMapper::toQuizResponse);

        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<QuizResponse>> getQuizzesByCourseIdAndYearGiven(
            Long courseId, int yearGiven, int page, int size) {
        log.info("Retrieving quizzes by course ID: {} and year given: {} with pagination - page: {}, size: {}",
                courseId, yearGiven, page, size);

        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            log.error("Course not found with ID: {}", courseId);
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quiz> quizPage = quizRepository.findByCourseIdAndYearGiven(courseId, yearGiven, pageable);

        PageResponse<QuizResponse> pageResponse = PaginationMapper.mapToPageResponse(
                quizPage, ResourceMapper::toQuizResponse);

        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<QuizResponse>> getQuizzesByCourseIdAndQuizTypeAndYearGiven(
            Long courseId, QuizType quizType, int yearGiven, int page, int size) {
        log.info("Retrieving quizzes by course ID: {}, quiz type: {} and year given: {} with pagination - page: {}, size: {}",
                courseId, quizType, yearGiven, page, size);

        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            log.error("Course not found with ID: {}", courseId);
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quiz> quizPage = quizRepository.findByCourseIdAndQuizTypeAndYearGiven(courseId, quizType, yearGiven, pageable);

        PageResponse<QuizResponse> pageResponse = PaginationMapper.mapToPageResponse(
                quizPage, ResourceMapper::toQuizResponse);

        return ApiResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<QuizResponse>> getQuizzesByDifficultyLevel(
            String difficultyLevel, int page, int size) {
        log.info("Retrieving quizzes by difficulty level: {} with pagination - page: {}, size: {}",
                difficultyLevel, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quiz> quizPage = quizRepository.findByDifficultyLevel(difficultyLevel, pageable);

        PageResponse<QuizResponse> pageResponse = PaginationMapper.mapToPageResponse(
                quizPage, ResourceMapper::toQuizResponse);

        return ApiResponse.success(pageResponse);
    }

    @Override
    public ApiResponse<QuizResponse> addQuestionsToQuiz(Long quizId, AddQuestionToQuizRequest request) {
        log.info("Adding questions to quiz with ID: {}", quizId);

        // Find quiz
        Quiz quiz = findQuizById(quizId);

        // Create and add questions
        List<Question> newQuestions = request.getQuestions().stream()
                .map(this::createQuestionFromRequest)
                .collect(Collectors.toList());

        quiz.getQuestions().addAll(newQuestions);
        quiz.setNumberOfQuestions(quiz.getQuestions().size());

        // Save updated quiz
        Quiz updatedQuiz = quizRepository.save(quiz);

        log.info("Questions added successfully to quiz with ID: {}", quizId);
        return ApiResponse.success("Questions added successfully", ResourceMapper.toQuizResponse(updatedQuiz));
    }

    @Override
    public ApiResponse<QuestionResponse> updateQuestionInQuiz(
            Long quizId, Long questionId, UpdateQuestionRequest request) {
        log.info("Updating question with ID: {} in quiz with ID: {}", questionId, quizId);

        // Find quiz
        Quiz quiz = findQuizById(quizId);

        // Find question in quiz
        Question question = quiz.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Question not found with ID: {} in quiz with ID: {}", questionId, quizId);
                    return new ResourceNotFoundException("Question not found with ID: " + questionId + " in quiz with ID: " + quizId);
                });

        // Update question fields
        question.setQuestionText(request.getQuestionText());
        question.setPossibleAnswers(request.getPossibleAnswers());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setExplanationSteps(request.getExplanationSteps());

        // Update referenced resource if provided
        if (request.getReferencedResourceId() != null) {
            Resource referencedResource = resourceRepository.findById(request.getReferencedResourceId())
                    .orElseThrow(() -> {
                        log.error("Resource not found with ID: {}", request.getReferencedResourceId());
                        return new ResourceNotFoundException("Resource not found with ID: " + request.getReferencedResourceId());
                    });
            question.setReferencedResource(referencedResource);
        } else {
            question.setReferencedResource(null);
        }

        // Save updated quiz
        quizRepository.save(quiz);

        log.info("Question updated successfully with ID: {} in quiz with ID: {}", questionId, quizId);
        return ApiResponse.success("Question updated successfully", ResourceMapper.toQuestionResponse(question));
    }

    @Override
    public ApiResponse<Void> removeQuestionFromQuiz(Long quizId, Long questionId) {
        log.info("Removing question with ID: {} from quiz with ID: {}", questionId, quizId);

        // Find quiz
        Quiz quiz = findQuizById(quizId);

        // Find and remove question
        boolean removed = quiz.getQuestions().removeIf(question -> question.getId().equals(questionId));

        if (!removed) {
            log.error("Question not found with ID: {} in quiz with ID: {}", questionId, quizId);
            throw new ResourceNotFoundException("Question not found with ID: " + questionId + " in quiz with ID: " + quizId);
        }

        // Update question count
        quiz.setNumberOfQuestions(quiz.getQuestions().size());

        // Save updated quiz
        quizRepository.save(quiz);

        log.info("Question removed successfully with ID: {} from quiz with ID: {}", questionId, quizId);
        return ApiResponse.success("Question removed successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Integer>> getDistinctYearGiven() {
        log.info("Retrieving distinct years given for quizzes");
        List<Integer> years = quizRepository.findDistinctYearGiven();
        return ApiResponse.success(years);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Integer>> getDistinctYearGivenByQuizType(QuizType quizType) {
        log.info("Retrieving distinct years given for quizzes by quiz type: {}", quizType);
        List<Integer> years = quizRepository.findDistinctYearGivenByQuizType(quizType);
        return ApiResponse.success(years);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Integer>> getDistinctYearGivenByCourseId(Long courseId) {
        log.info("Retrieving distinct years given for quizzes by course ID: {}", courseId);

        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            log.error("Course not found with ID: {}", courseId);
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }

        List<Integer> years = quizRepository.findDistinctYearGivenByCourseId(courseId);
        return ApiResponse.success(years);
    }

    /**
     * Helper method to find a quiz by ID.
     *
     * @param id the quiz ID
     * @return the quiz entity
     * @throws ResourceNotFoundException if quiz not found
     */
    private Quiz findQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Quiz not found with ID: {}", id);
                    return new ResourceNotFoundException("Quiz not found with ID: " + id);
                });
    }

    /**
     * Helper method to create a Question entity from a CreateQuestionRequest.
     *
     * @param request the question creation request
     * @return the created question entity
     */
    private Question createQuestionFromRequest(CreateQuestionRequest request) {
        Question question = Question.builder()
                .questionText(request.getQuestionText())
                .possibleAnswers(request.getPossibleAnswers())
                .correctAnswer(request.getCorrectAnswer())
                .explanationSteps(request.getExplanationSteps())
                .build();

        // Set referenced resource if provided
        if (request.getReferencedResourceId() != null) {
            Resource referencedResource = resourceRepository.findById(request.getReferencedResourceId())
                    .orElseThrow(() -> {
                        log.error("Resource not found with ID: {}", request.getReferencedResourceId());
                        return new ResourceNotFoundException("Resource not found with ID: " + request.getReferencedResourceId());
                    });
            question.setReferencedResource(referencedResource);
        }

        return question;
    }
}
