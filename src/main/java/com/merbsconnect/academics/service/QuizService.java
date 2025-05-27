package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.request.AddQuestionToQuizRequest;
import com.merbsconnect.academics.dto.request.CreateQuizRequest;
import com.merbsconnect.academics.dto.request.UpdateQuestionRequest;
import com.merbsconnect.academics.dto.request.UpdateQuizRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.QuestionResponse;
import com.merbsconnect.academics.dto.response.QuizResponse;
import com.merbsconnect.enums.QuizType;

import java.util.List;

/**
 * Service interface for managing Quiz entities.
 */
public interface QuizService {
    
    /**
     * Creates a new quiz.
     *
     * @param request the quiz creation request
     * @return API response containing the created quiz
     */
    ApiResponse<QuizResponse> createQuiz(CreateQuizRequest request);
    
    /**
     * Updates an existing quiz.
     *
     * @param id the quiz ID
     * @param request the quiz update request
     * @return API response containing the updated quiz
     */
    ApiResponse<QuizResponse> updateQuiz(Long id, UpdateQuizRequest request);
    
    /**
     * Retrieves a quiz by its ID.
     *
     * @param id the quiz ID
     * @return API response containing the quiz
     */
    ApiResponse<QuizResponse> getQuizById(Long id);
    
    /**
     * Deletes a quiz by its ID.
     *
     * @param id the quiz ID
     * @return API response indicating success or failure
     */
    ApiResponse<Void> deleteQuiz(Long id);
    
    /**
     * Retrieves all quizzes with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated quizzes
     */
    ApiResponse<PageResponse<QuizResponse>> getAllQuizzes(int page, int size);
    
    /**
     * Retrieves quizzes by quiz type with pagination.
     *
     * @param quizType the quiz type
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated quizzes
     */
    ApiResponse<PageResponse<QuizResponse>> getQuizzesByQuizType(QuizType quizType, int page, int size);
    
    /**
     * Retrieves quizzes by year given with pagination.
     *
     * @param yearGiven the year given
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated quizzes
     */
    ApiResponse<PageResponse<QuizResponse>> getQuizzesByYearGiven(int yearGiven, int page, int size);
    
    /**
     * Retrieves quizzes by quiz type and year given with pagination.
     *
     * @param quizType the quiz type
     * @param yearGiven the year given
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated quizzes
     */
    ApiResponse<PageResponse<QuizResponse>> getQuizzesByQuizTypeAndYearGiven(
            QuizType quizType, int yearGiven, int page, int size);
    
    /**
     * Retrieves quizzes by course ID and quiz type with pagination.
     *
     * @param courseId the course ID
     * @param quizType the quiz type
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated quizzes
     */
    ApiResponse<PageResponse<QuizResponse>> getQuizzesByCourseIdAndQuizType(
            Long courseId, QuizType quizType, int page, int size);
    
    /**
     * Retrieves quizzes by course ID and year given with pagination.
     *
     * @param courseId the course ID
     * @param yearGiven the year given
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated quizzes
     */
    ApiResponse<PageResponse<QuizResponse>> getQuizzesByCourseIdAndYearGiven(
            Long courseId, int yearGiven, int page, int size);
    
    /**
     * Retrieves quizzes by course ID, quiz type, and year given with pagination.
     *
     * @param courseId the course ID
     * @param quizType the quiz type
     * @param yearGiven the year given
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated quizzes
     */
    ApiResponse<PageResponse<QuizResponse>> getQuizzesByCourseIdAndQuizTypeAndYearGiven(
            Long courseId, QuizType quizType, int yearGiven, int page, int size);
    
    /**
     * Retrieves quizzes by difficulty level with pagination.
     *
     * @param difficultyLevel the difficulty level
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated quizzes
     */
    ApiResponse<PageResponse<QuizResponse>> getQuizzesByDifficultyLevel(
            String difficultyLevel, int page, int size);
    
    /**
     * Adds questions to an existing quiz.
     *
     * @param quizId the quiz ID
     * @param request the request containing questions to add
     * @return API response containing the updated quiz
     */
    ApiResponse<QuizResponse> addQuestionsToQuiz(Long quizId, AddQuestionToQuizRequest request);
    
    /**
     * Updates a question in a quiz.
     *
     * @param quizId the quiz ID
     * @param questionId the question ID
     * @param request the question update request
     * @return API response containing the updated question
     */
    ApiResponse<QuestionResponse> updateQuestionInQuiz(
            Long quizId, Long questionId, UpdateQuestionRequest request);
    
    /**
     * Removes a question from a quiz.
     *
     * @param quizId the quiz ID
     * @param questionId the question ID
     * @return API response indicating success or failure
     */
    ApiResponse<Void> removeQuestionFromQuiz(Long quizId, Long questionId);
    
    /**
     * Retrieves all distinct years given for quizzes.
     *
     * @return API response containing list of distinct years
     */
    ApiResponse<List<Integer>> getDistinctYearGiven();
    
    /**
     * Retrieves distinct years given for quizzes by quiz type.
     *
     * @param quizType the quiz type
     * @return API response containing list of distinct years
     */
    ApiResponse<List<Integer>> getDistinctYearGivenByQuizType(QuizType quizType);
    
    /**
     * Retrieves distinct years given for quizzes by course ID.
     *
     * @param courseId the course ID
     * @return API response containing list of distinct years
     */
    ApiResponse<List<Integer>> getDistinctYearGivenByCourseId(Long courseId);
}
