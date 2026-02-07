package com.merbsconnect.events.service.impl;

import com.merbsconnect.events.dto.request.CreateArticleRequest;
import com.merbsconnect.events.dto.response.ArticleResponse;
import com.merbsconnect.events.model.Article;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.repository.ArticleRepository;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.service.ArticleService;
import com.merbsconnect.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ArticleService for managing event articles/transcripts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ArticleResponse createArticle(Long eventId, CreateArticleRequest request) {
        log.info("Creating article for event {}: {}", eventId, request.getTitle());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        Article article = Article.builder()
                .event(event)
                .speakerName(request.getSpeakerName())
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();

        Article savedArticle = articleRepository.save(article);
        log.info("Article created with id: {}", savedArticle.getId());

        return mapToResponse(savedArticle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesForEvent(Long eventId) {
        log.debug("Fetching articles for event {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }

        return articleRepository.findByEventId(eventId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long articleId) {
        log.debug("Fetching article with id {}", articleId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        return mapToResponse(article);
    }

    @Override
    @Transactional
    public ArticleResponse updateArticle(Long articleId, CreateArticleRequest request) {
        log.info("Updating article {}", articleId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        article.setSpeakerName(request.getSpeakerName());
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setImageUrl(request.getImageUrl());

        Article updatedArticle = articleRepository.save(article);
        log.info("Article {} updated successfully", articleId);

        return mapToResponse(updatedArticle);
    }

    @Override
    @Transactional
    public void deleteArticle(Long articleId) {
        log.info("Deleting article {}", articleId);

        if (!articleRepository.existsById(articleId)) {
            throw new ResourceNotFoundException("Article not found with id: " + articleId);
        }

        articleRepository.deleteById(articleId);
        log.info("Article {} deleted successfully", articleId);
    }

    /**
     * Maps an Article entity to an ArticleResponse DTO.
     */
    private ArticleResponse mapToResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .eventId(article.getEvent().getId())
                .speakerName(article.getSpeakerName())
                .title(article.getTitle())
                .content(article.getContent())
                .imageUrl(article.getImageUrl())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}
