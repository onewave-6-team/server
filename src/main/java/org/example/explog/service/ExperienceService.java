package org.example.explog.service;

import lombok.RequiredArgsConstructor;
import org.example.explog.domain.Experience;
import org.example.explog.domain.Folder;
import org.example.explog.domain.User;
import org.example.explog.domain.enums.SourceType;
import org.example.explog.dto.request.ExperienceSaveRequest;
import org.example.explog.dto.response.*;
import org.example.explog.repository.ExperienceRepository;
import org.example.explog.repository.FolderRepository;
import org.example.explog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    // 1번 기능: 최근 경험 조회 (Top 5)
    public List<ExperienceSummaryDto> getRecentExperiences() {
        // [하드코딩] 무조건 1번 유저 데이터만 가져옴
        User user = userRepository.findById(1L).orElseThrow();
        
        return experienceRepository.findTop5ByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // 2번 기능: 경험 수기 작성
    @Transactional
    public void createExperience(ExperienceSaveRequest request) {
        // [하드코딩] 무조건 1번 유저가 작성한 것으로 저장
        User user = userRepository.findById(1L).orElseThrow();
        
        Experience experience = Experience.builder()
                .input(request.input())
                .user(user) // 1번 유저 세팅
                .sourceType(SourceType.MANUAL)
                .sourceUrl(null)
                .build();
        
        experienceRepository.save(experience);
    }

    // 3번 기능: 폴더 리스트 조회
    public List<FolderItemDto> getFolders() {
        // [하드코딩] 1번 유저의 폴더만 조회
        User user = userRepository.findById(1L).orElseThrow();
        
        return folderRepository.findAllByUserOrderByUpdatedAtDesc(user).stream()
                .map(folder -> new FolderItemDto(
                        folder.getId(),
                        folder.getCategory(),
                        folder.getName(),
                        folder.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 4번 기능: 폴더별 경험 리스트 조회
    public List<ExperienceSummaryDto> getExperiencesByFolder(Long folderId) {
        // 폴더가 존재하는지만 확인 (내 폴더인지 체크하는 로직 생략 - 해커톤이니까!)
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 폴더입니다."));
        
        return experienceRepository.findAllByFolderOrderByCreatedAtDesc(folder).stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // 5번 기능: 경험 상세 조회
    public ExperienceDetailResponse getExperienceDetail(Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경험입니다."));

        return new ExperienceDetailResponse(
                experience.getId(),
                experience.getCategory(),
                experience.getSourceType(),
                experience.getSourceUrl(),
                experience.getTitle(),
                experience.getCreatedAt(),
                experience.getContent()
        );
    }

    // Entity -> DTO 변환 메서드
    private ExperienceSummaryDto mapToSummaryDto(Experience experience) {
        return new ExperienceSummaryDto(
                experience.getId(),
                experience.getCategory(),
                experience.getSourceType(),
                experience.getSourceUrl(),
                experience.getTitle(),
                experience.getSummary(),
                experience.getCreatedAt()
        );
    }
}