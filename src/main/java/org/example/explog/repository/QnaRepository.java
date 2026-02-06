package org.example.explog.repository;

import org.example.explog.domain.Qna;
import org.example.explog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    // 6번 API: 답변을 기다리는(Answer가 null인) 질문 리스트 조회
    // 관계: Qna -> Experience -> User
    // 해석: Experience의 User가 일치하고 AND Answer가 Null인 것들을 최신순으로 찾아라.
    List<Qna> findAllByExperience_UserAndAnswerIsNullOrderByCreatedAtDesc(User user);
    
    // 특정 경험에 달린 질문들 조회 (상세 페이지용)
    List<Qna> findAllByExperienceId(Long experienceId);
}