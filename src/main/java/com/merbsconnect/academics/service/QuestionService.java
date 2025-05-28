package com.merbsconnect.academics.service;

import com.merbsconnect.academics.dto.request.CreateStandaloneQuestionRequest;
import com.merbsconnect.academics.dto.request.QuestionSearchRequest;
import com.merbsconnect.academics.dto.request.UpdateQuestionRequest;
import com.merbsconnect.academics.dto.response.ApiResponse;
import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.academics.dto.response.QuestionResponse;

import java.util.List;

public interface QuestionService {
    
    /**
     * Creates a standalone question in the question bank
     *
     * @param request the question creation request
     * @return API response containing the created question
     */
    ApiResponse<QuestionResponse> createQuestion(CreateStandaloneQuestionRequest request);
    
    /**
     * Retrieves a question by its ID
     *
     * @param questionId the question ID
     * @return API response containing the question
     */
    ApiResponse<QuestionResponse> getQuestionById(Long questionId);
    
    /**
     * Updates an existing question
     *
     * @param questionId the question ID
     * @param request the question update request
     * @return API response containing the updated question
     */
    ApiResponse<QuestionResponse> updateQuestion(Long questionId, UpdateQuestionRequest request);
    
    /**
     * Deletes a question by its ID
     *
     * @param questionId the question ID
     * @return API response indicating success or failure
     */
    ApiResponse<Void> deleteQuestion(Long questionId);
    
    /**
     * Searches questions based on various criteria
     *
     * @param request the search request containing filters
     * @return API response containing paginated questions
     */
    ApiResponse<PageResponse<QuestionResponse>> searchQuestions(QuestionSearchRequest request);
    
    /**
     * Adds tags to a question
     *
     * @param questionId the question ID
     * @param tags the list of tags to add
     * @return API response containing the updated question
     */
    ApiResponse<QuestionResponse> addTagsToQuestion(Long questionId, List<String> tags);
    
    /**
     * Removes tags from a question
     *
     * @param questionId the question ID
     * @param tags the list of tags to remove
     * @return API response containing the updated question
     */
    ApiResponse<QuestionResponse> removeTagsFromQuestion(Long questionId, List<String> tags);
    
    /**
     * Retrieves questions by tag
     *
     * @param tag the tag to search for
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated questions
     */
    ApiResponse<PageResponse<QuestionResponse>> getQuestionsByTag(String tag, int page, int size);
    
    /**
     * Retrieves questions that are not used in any quiz
     *
     * @param page the page number
     * @param size the page size
     * @return API response containing paginated questions
     */
    ApiResponse<PageResponse<QuestionResponse>> getUnusedQuestions(int page, int size);
}