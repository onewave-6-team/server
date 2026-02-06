package org.example.explog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.explog.domain.Experience;
import org.example.explog.domain.Qna;
import org.example.explog.domain.User;
import org.example.explog.domain.enums.ExperienceStatus;
import org.example.explog.dto.request.QuestionAnswerRequest;
import org.example.explog.dto.response.QuestionSummaryDto;
import org.example.explog.repository.QnaRepository;
import org.example.explog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    // 6번 기능: 답변을 기다리는 질문 리스트
    public List<QuestionSummaryDto> getUnansweredQuestions() {
        User user = userRepository.findById(1L).orElseThrow();

        return qnaRepository.findAllByExperience_UserAndAnswerIsNullOrderByCreatedAtDesc(user).stream()
                .map(qna -> new QuestionSummaryDto(
                        qna.getId(),
                        qna.getExperience().getId(),
                        qna.getExperience().getSourceType(),
                        qna.getExperience().getTitle(),
                        qna.getQuestion(),
                        qna.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 7번 기능: 답변 저장 (데이터 수정이 일어나므로 트랜잭션 필수!)
    @Transactional // ⭐ [핵심 수정] 쓰기 가능한 트랜잭션으로 오버라이드
    public void saveAnswer(Long questionId, QuestionAnswerRequest request) {
        // 1. 질문 찾기
        Qna qna = qnaRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다. ID: " + questionId));

        Experience experience = qna.getExperience();
        log.info("답변 통합 시작 - Question ID: {}, Experience ID: {}", questionId, experience.getId());

        // 2. 답변 업데이트 (JPA Dirty Checking에 의해 자동 저장됨)
        qna.updateAnswer(request.answer());

        // 3. Gemini 호출
        GeminiService.ContentUpdateResponse response = geminiService.integrateAnswer(
                experience.getContent(),
                qna.getQuestion(),
                request.answer()
        );

        // 로그 출력
        log.info("👉 [Gemini 응답 객체 전체]: {}", response);
        log.info("🤖 Gemini 통합 결과 상세\n [New Title]: {}\n [New Summary]: {}\n [New Content]: \n{}",
                response.title(),
                response.summary(),
                response.updated_content());

        // 4. 경험 데이터 업데이트 (JPA Dirty Checking에 의해 자동 저장됨)
        experience.updateTitle(response.title());
        experience.updateSummary(response.summary());
        experience.updateContent(response.updated_content());

        // 5. 완료 상태 체크
        boolean hasRemainingQuestions = qnaRepository.existsByExperienceAndAnswerIsNull(experience);

        if (!hasRemainingQuestions) {
            experience.updateStatus(ExperienceStatus.COMPLETED);
            log.info("🎉 모든 질문 답변 완료! 상태 변경: COMPLETED (Exp ID: {})", experience.getId());
        } else {
            log.info("⏳ 아직 답변하지 않은 질문이 남아있습니다.");
        }

        if (experience.getFolder() != null) {
            experience.getFolder().updateTimestamp();
        }

        // 메서드가 끝날 때 @Transactional에 의해 변경사항이 DB에 commit 됩니다.
    }
}