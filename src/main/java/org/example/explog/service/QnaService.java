package org.example.explog.service;

import lombok.RequiredArgsConstructor;
import org.example.explog.domain.Qna;
import org.example.explog.domain.User;
import org.example.explog.dto.request.QuestionAnswerRequest;
import org.example.explog.dto.response.QuestionSummaryDto;
import org.example.explog.repository.QnaRepository;
import org.example.explog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;

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
    @Transactional
    public void saveAnswer(Long questionId, QuestionAnswerRequest request) {
        Qna qna = qnaRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다."));
        
        qna.updateAnswer(request.answer());
    }
}