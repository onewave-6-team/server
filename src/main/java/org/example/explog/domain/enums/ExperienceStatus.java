package org.example.explog.domain.enums;

public enum ExperienceStatus {
    ANALYZING,    // AI가 분석 중
    QNA_WAITING,  // 질문 생성 완료, 답변 대기 중
    COMPLETED     // 답변 완료
}