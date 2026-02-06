package org.example.explog.dto.response;

import org.example.explog.domain.enums.SourceType;
import java.time.LocalDateTime;

public record QuestionSummaryDto(
        Long questionId,      // Qna 엔티티의 ID
        SourceType sourceType, // 연관된 경험의 출처 (Experience.sourceType)
        String title,         // 연관된 경험의 제목 (Experience.title)
        String content,       // 질문 내용 (Qna.question)
        LocalDateTime createdAt // 질문 생성일
) {}