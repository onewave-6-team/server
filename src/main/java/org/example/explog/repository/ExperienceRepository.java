package org.example.explog.repository;

import org.example.explog.domain.Experience;
import org.example.explog.domain.Folder;
import org.example.explog.domain.User;
import org.example.explog.domain.enums.ExperienceStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    // 1. [개수 제한용] Pageable을 받아서 원하는 개수만큼 잘라서 조회
    List<Experience> findByUserAndStatusNotOrderByCreatedAtDesc(User user, ExperienceStatus status, Pageable pageable);

    // 2. [전체 조회용] 페이징 없이 조건에 맞는 모든 데이터 조회
    List<Experience> findAllByUserAndStatusNotOrderByCreatedAtDesc(User user, ExperienceStatus status);

    // 4번 API: 특정 폴더에 속한 경험 리스트 조회
    List<Experience> findAllByFolderOrderByCreatedAtDesc(Folder folder);
    
    // 전체 경험 조회 (필요 시)
    List<Experience> findAllByUserOrderByCreatedAtDesc(User user);
}