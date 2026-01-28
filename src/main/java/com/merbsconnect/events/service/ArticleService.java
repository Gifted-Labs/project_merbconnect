package com.merbsconnect.events.service;

import com.merbsconnect.events.dto.request.CreateArticleRequest;
import com.merbsconnect.events.dto.response.ArticleResponse;

import java.util.List;

/**
 * Service interface for event article operations.
 */
public interface ArticleService {

    /**
     * Creates a new article for an event.
     * Admin-only operation.
     *
     * @param eventId The event ID
     * @param request The article request
     * @return The created article
     */
    ArticleResponse createArticle(Long eventId, CreateArticleRequest request);

    /**
     * Gets all articles for an event.
     *
     * @param eventId The event ID
     * @return List of articles
     */
    List<ArticleResponse> getArticlesForEvent(Long eventId);

    /**
     * Gets a single article by ID.
     *
     * @param articleId The article ID
     * @return The article
     */
    ArticleResponse getArticleById(Long articleId);

    /**
     * Updates an existing article.
     * Admin-only operation.
     *
     * @param articleId The article ID
     * @param request   The update request
     * @return The updated article
     */
    ArticleResponse updateArticle(Long articleId, CreateArticleRequest request);

    /**
     * Deletes an article.
     * Admin-only operation.
     *
     * @param articleId The article ID
     */
    void deleteArticle(Long articleId);
}
