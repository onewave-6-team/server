package org.example.explog.dto.response;

import java.util.List;

public record QuestionListResponse(
        List<QuestionSummaryDto> questions
) {
    public static QuestionListResponse from(List<QuestionSummaryDto> questions) {
        return new QuestionListResponse(questions);
    }
}