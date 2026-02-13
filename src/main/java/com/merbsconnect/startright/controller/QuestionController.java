package com.merbsconnect.startright.controller;

import com.merbsconnect.startright.dto.request.QuestionRequestDto;
import com.merbsconnect.startright.dto.response.QuestionResponseDto;
import com.merbsconnect.startright.enums.QuestionStatus;
import com.merbsconnect.startright.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/startright/questions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Submit a new question (PUBLIC - no auth required)
     */
    @PostMapping
    public ResponseEntity<QuestionResponseDto> submitQuestion(
            @Valid @RequestBody QuestionRequestDto requestDto) {
        QuestionResponseDto response = questionService.submitQuestion(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all questions with optional status filter (ADMIN)
     */
    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestions(
            @RequestParam(required = false) QuestionStatus status) {
        List<QuestionResponseDto> questions = questionService.getAllQuestions(status);
        return ResponseEntity.ok(questions);
    }

    /**
     * Update question status (ADMIN)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<QuestionResponseDto> updateQuestionStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        QuestionStatus newStatus = QuestionStatus.valueOf(statusUpdate.get("status"));
        QuestionResponseDto response = questionService.updateQuestionStatus(id, newStatus);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a question (ADMIN)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get question counts (ADMIN)
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getQuestionCounts() {
        long total = questionService.getQuestionCount(null);
        long pending = questionService.getQuestionCount(QuestionStatus.PENDING);
        long answered = questionService.getQuestionCount(QuestionStatus.ANSWERED);
        long dismissed = questionService.getQuestionCount(QuestionStatus.DISMISSED);

        return ResponseEntity.ok(Map.of(
                "total", total,
                "pending", pending,
                "answered", answered,
                "dismissed", dismissed));
    }
}
