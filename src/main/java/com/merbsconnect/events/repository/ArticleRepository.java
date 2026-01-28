package com.merbsconnect.events.repository;

import com.merbsconnect.events.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for event articles/transcripts.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Find all articles for an event.
     */
    List<Article> findByEventId(Long eventId);

    /**
     * Find articles by event and speaker name.
     */
    List<Article> findByEventIdAndSpeakerName(Long eventId, String speakerName);

    /**
     * Check if an article exists for a speaker at an event.
     */
    boolean existsByEventIdAndSpeakerNameAndTitle(Long eventId, String speakerName, String title);
}
