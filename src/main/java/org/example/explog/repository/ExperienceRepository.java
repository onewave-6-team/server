package org.example.explog.repository;

import org.example.explog.domain.Experience;
import org.example.explog.domain.Folder;
import org.example.explog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    // 1번 API: 최근 작성한 경험 리스트 조회 (Top 5)
    // Spring Data JPA가 'Top5' 키워드를 보고 알아서 LIMIT 5를 걸어줍니다.
    List<Experience> findTop5ByUserOrderByCreatedAtDesc(User user);

    // 4번 API: 특정 폴더에 속한 경험 리스트 조회
    List<Experience> findAllByFolderOrderByCreatedAtDesc(Folder folder);
    
    // 전체 경험 조회 (필요 시)
    List<Experience> findAllByUserOrderByCreatedAtDesc(User user);
}