package com.merbsconnect.startright.service.impl;

import com.merbsconnect.startright.dto.request.QuestionRequestDto;
import com.merbsconnect.startright.dto.response.QuestionResponseDto;
import com.merbsconnect.startright.entity.Question;
import com.merbsconnect.startright.enums.QuestionStatus;
import com.merbsconnect.startright.repository.QuestionRepository;
import com.merbsconnect.startright.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public QuestionResponseDto submitQuestion(QuestionRequestDto requestDto) {
        log.info("Submitting new question");

        Question question = Question.builder()
                .content(requestDto.getContent())
                .studentName(Boolean.TRUE.equals(requestDto.getIsAnonymous()) ? null : requestDto.getStudentName())
                .program(requestDto.getProgram())
                .academicLevel(requestDto.getAcademicLevel())
                .isAnonymous(requestDto.getIsAnonymous())
                .build();

        Question saved = questionRepository.save(question);
        log.info("Question submitted with ID: {}", saved.getId());
        return mapToDto(saved);
    }

    @Override
    public List<QuestionResponseDto> getAllQuestions(QuestionStatus status) {
        List<Question> questions;
        if (status != null) {
            questions = questionRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            questions = questionRepository.findAllByOrderByCreatedAtDesc();
        }
        return questions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public QuestionResponseDto updateQuestionStatus(Long questionId, QuestionStatus status) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));

        question.setStatus(status);
        Question updated = questionRepository.save(question);
        log.info("Question {} status updated to {}", questionId, status);
        return mapToDto(updated);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new RuntimeException("Question not found with ID: " + questionId);
        }
        questionRepository.deleteById(questionId);
        log.info("Question {} deleted", questionId);
    }

    @Override
    public long getQuestionCount(QuestionStatus status) {
        if (status != null) {
            return questionRepository.countByStatus(status);
        }
        return questionRepository.count();
    }

    private QuestionResponseDto mapToDto(Question question) {
        return QuestionResponseDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .studentName(question.getStudentName())
                .program(question.getProgram())
                .academicLevel(question.getAcademicLevel())
                .isAnonymous(question.getIsAnonymous())
                .status(question.getStatus())
                .createdAt(question.getCreatedAt())
                .build();
    }
}
