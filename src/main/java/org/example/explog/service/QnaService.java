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
@Transactional(readOnly = true)
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    // 6번 기능: 답변을 기다리는 질문 리스트
    public List<QuestionSummaryDto> getUnansweredQuestions() {
        // [하드코딩] 1번 유저 찾기
        User user = userRepository.findById(1L).orElseThrow();
        
        // 1번 유저의 경험 중에서 답변이 없는 질문만 조회
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

    // 7번 기능: 답변 저장
    // 2번 기능: AI 질문에 대한 답변 저장 + 경험 데이터(제목/요약/본문) 전체 업데이트
    public void saveAnswer(Long questionId, QuestionAnswerRequest request) {
        // 1. 질문 찾기
        Qna qna = qnaRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다. ID: " + questionId));

        Experience experience = qna.getExperience();
        log.info("답변 통합 시작 - Question ID: {}, Experience ID: {}", questionId, experience.getId());

        // 2. 답변 저장
        qna.updateAnswer(request.answer());
        qnaRepository.save(qna);

        // 3. Gemini 호출 (기존 내용 + 질문 + 내 답변 => 새로운 제목/요약/본문)
        GeminiService.ContentUpdateResponse response = geminiService.integrateAnswer(
                experience.getContent(), // 현재 내용
                qna.getQuestion(),       // AI 질문
                request.answer()         // 내 답변
        );

        // ⭐ [로그 추가] Gemini 응답 데이터 전체 (원문 확인용) & New Content 출력
        log.info("👉 [Gemini 응답 객체 전체]: {}", response); // DTO 전체 내용 출력
        log.info("🤖 Gemini 통합 결과 상세\n [New Title]: {}\n [New Summary]: {}\n [New Content]: \n{}",
                response.title(),
                response.summary(),
                response.updated_content()); // 본문은 길 수 있으니 줄바꿈 후 출력

        // 4. ⭐ 경험 데이터 전체 업데이트
        experience.updateTitle(response.title());
        experience.updateSummary(response.summary());
        experience.updateContent(response.updated_content());

        // 5. 모든 질문에 답변이 달렸는지 확인 후 상태 완료 처리?

        boolean hasRemainingQuestions = qnaRepository.existsByExperienceAndAnswerIsNull(experience);

        if (!hasRemainingQuestions) {
            experience.updateStatus(ExperienceStatus.COMPLETED);
            log.info("🎉 모든 질문 답변 완료! 상태 변경: QNA_WAITING -> COMPLETED (Experience ID: {})", experience.getId());
        } else {
            log.info("⏳ 아직 답변하지 않은 질문이 남아있습니다.");
        }
    }
}