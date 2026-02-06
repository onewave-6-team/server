package org.example.explog.dto.response;

import org.example.explog.domain.enums.CategoryType;
import org.example.explog.domain.enums.SourceType;
import java.time.LocalDateTime;

public record ExperienceSummaryDto(
        Long experienceId,
        CategoryType experienceType,
        SourceType sourceType,
        String sourceUrl,
        String title,
        String summary,
        LocalDateTime createdAt
) {}