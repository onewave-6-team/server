package org.example.explog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.explog.dto.request.QuestionAnswerRequest;
import org.example.explog.dto.response.QuestionListResponse;
import org.example.explog.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Question", description = "AI 질문/답변 API")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QnaService qnaService;

    @Operation(summary = "답변을 기다리는 질문 리스트 조회")
    @GetMapping
    public ResponseEntity<QuestionListResponse> getQuestions() {
        return ResponseEntity.ok(QuestionListResponse.from(qnaService.getUnansweredQuestions()));
    }

    @Operation(summary = "AI 질문에 답변하기")
    @PostMapping("/{questionId}/answer")
    public ResponseEntity<Void> answerQuestion(
            @PathVariable Long questionId,
            @RequestBody @Valid QuestionAnswerRequest request
    ) {
        qnaService.saveAnswer(questionId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(null);
    }
}