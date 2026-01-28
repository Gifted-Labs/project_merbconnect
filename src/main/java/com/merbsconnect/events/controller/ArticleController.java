package com.merbsconnect.events.controller;

import com.merbsconnect.events.dto.request.CreateArticleRequest;
import com.merbsconnect.events.dto.response.ArticleResponse;
import com.merbsconnect.events.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for event article/transcript operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events/{eventId}/articles")
@RequiredArgsConstructor
@Tag(name = "Event Articles", description = "Event article and transcript operations")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * Create a new article for an event.
     * Admin-only operation.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    @Operation(summary = "Create an article", description = "Create a new article (admin-only)")
    public ResponseEntity<ArticleResponse> createArticle(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateArticleRequest request) {

        log.info("Creating article for event {}: {}", eventId, request.getTitle());
        ArticleResponse response = articleService.createArticle(eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all articles for an event.
     * Public endpoint.
     */
    @GetMapping
    @Operation(summary = "Get articles", description = "Get all articles for an event")
    public ResponseEntity<List<ArticleResponse>> getArticles(@PathVariable Long eventId) {
        log.debug("Fetching articles for event {}", eventId);
        List<ArticleResponse> articles = articleService.getArticlesForEvent(eventId);
        return ResponseEntity.ok(articles);
    }

    /**
     * Get a single article by ID.
     * Public endpoint.
     */
    @GetMapping("/{articleId}")
    @Operation(summary = "Get article", description = "Get a single article by ID")
    public ResponseEntity<ArticleResponse> getArticle(
            @PathVariable Long eventId,
            @PathVariable Long articleId) {

        log.debug("Fetching article {} for event {}", articleId, eventId);
        ArticleResponse article = articleService.getArticleById(articleId);
        return ResponseEntity.ok(article);
    }

    /**
     * Update an existing article.
     * Admin-only operation.
     */
    @PutMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    @Operation(summary = "Update an article", description = "Update an existing article (admin-only)")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long eventId,
            @PathVariable Long articleId,
            @Valid @RequestBody CreateArticleRequest request) {

        log.info("Updating article {} for event {}", articleId, eventId);
        ArticleResponse response = articleService.updateArticle(articleId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an article.
     * Admin-only operation.
     */
    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    @Operation(summary = "Delete an article", description = "Delete an article (admin-only)")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable Long eventId,
            @PathVariable Long articleId) {

        log.info("Deleting article {} for event {}", articleId, eventId);
        articleService.deleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }
}
