package org.example.explog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record QuestionAnswerRequest(
        @NotBlank(message = "답변 내용은 필수입니다.")
        String answer
) {}