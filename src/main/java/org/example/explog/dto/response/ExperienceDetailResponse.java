package org.example.explog.dto.response;

import org.example.explog.domain.enums.CategoryType;
import org.example.explog.domain.enums.SourceType;
import java.time.LocalDateTime;

public record ExperienceDetailResponse(
        Long experienceId,
        CategoryType experienceType, // JSON의 "experienceType"과 매핑
        SourceType sourceType,
        String sourceUrl,
        String title,
        LocalDateTime createdAt,
        String content // 경험 상세 내용
) {}