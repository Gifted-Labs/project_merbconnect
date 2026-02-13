package com.merbsconnect.startright.repository;

import com.merbsconnect.startright.entity.Question;
import com.merbsconnect.startright.enums.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByStatusOrderByCreatedAtDesc(QuestionStatus status);

    List<Question> findAllByOrderByCreatedAtDesc();

    long countByStatus(QuestionStatus status);
}
