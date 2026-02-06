package org.example.explog.dto.response;

import java.util.List;

public record ExperienceListResponse(
        List<ExperienceSummaryDto> experiences
) {
    // 편의상 정적 팩토리 메서드 추가 (Controller에서 쓰기 편함)
    public static ExperienceListResponse from(List<ExperienceSummaryDto> experiences) {
        return new ExperienceListResponse(experiences);
    }
}