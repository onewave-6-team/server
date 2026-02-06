package org.example.explog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.explog.domain.Experience;
import org.example.explog.domain.Qna;
import org.example.explog.domain.enums.CategoryType;
import org.example.explog.domain.enums.ExperienceStatus;
import org.example.explog.repository.ExperienceRepository;
import org.example.explog.repository.QnaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private final GeminiService geminiService;
    private final ExperienceRepository experienceRepository;
    private final QnaRepository qnaRepository;

    @Async
    @Transactional
    public void runAnalysis(Long experienceId, String userInput) {
        log.info("========== [Async] AI 분석 및 내용 생성 시작 (ID: {}) ==========", experienceId);

        try {
            // 1. 엔티티 조회
            Experience experience = experienceRepository.findById(experienceId)
                    .orElseThrow(() -> new IllegalArgumentException("경험을 찾을 수 없습니다."));

            // 2. Gemini 호출
            GeminiService.QuestionGenerationResponse response = geminiService.analyzeAndGenerateQuestions(userInput);
            
            log.info("👉 [Gemini 응답]\n - 제목: {}\n - 요약: {}\n - 카테고리: {}", 
                    response.title(), response.summary(), response.category());

            // 3. ⭐ [핵심] 제목, 요약, 다듬어진 본문 업데이트
            experience.updateTitle(response.title());
            experience.updateSummary(response.summary());
            
            // AI가 다듬어준 본문이 있다면 교체 (null 체크)
            if (response.refined_content() != null && !response.refined_content().isBlank()) {
                experience.updateContent(response.refined_content());
                log.info("✅ 본문 내용이 AI가 다듬은 버전으로 업데이트되었습니다.");
            }

            // 4. 카테고리 업데이트
            try {
                experience.updateCategory(CategoryType.valueOf(response.category()));
            } catch (Exception e) {
                log.warn("카테고리 매핑 실패, 기본값 적용");
                experience.updateCategory(CategoryType.DEVELOPMENT);
            }

            // 5. 질문 저장
            List<Qna> qnaList = response.questions().stream()
                    .map(questionText -> Qna.builder()
                            .experience(experience)
                            .question(questionText)
                            .step(1)
                            .build())
                    .toList();
            qnaRepository.saveAll(qnaList);

            // 6. 상태 업데이트
            if (qnaList.isEmpty()) {
                experience.updateStatus(ExperienceStatus.COMPLETED);
            } else {
                experience.updateStatus(ExperienceStatus.QNA_WAITING);
            }

            log.info("========== [Async] 분석 완료 (상태: {}) ==========", experience.getStatus());

            if (experience.getFolder() != null) {
                experience.getFolder().updateTimestamp();
            }

        } catch (Exception e) {
            log.error("❌ AI 분석 중 에러 발생", e);
        }
    }
}