package com.merbsconnect.startright.service;

import com.merbsconnect.startright.dto.request.QuestionRequestDto;
import com.merbsconnect.startright.dto.response.QuestionResponseDto;
import com.merbsconnect.startright.enums.QuestionStatus;

import java.util.List;

public interface QuestionService {

    QuestionResponseDto submitQuestion(QuestionRequestDto requestDto);

    List<QuestionResponseDto> getAllQuestions(QuestionStatus status);

    QuestionResponseDto updateQuestionStatus(Long questionId, QuestionStatus status);

    void deleteQuestion(Long questionId);

    long getQuestionCount(QuestionStatus status);
}
