package org.example.explog.service;

import lombok.RequiredArgsConstructor;
import org.example.explog.domain.Experience;
import org.example.explog.domain.Folder;
import org.example.explog.domain.User;
import org.example.explog.domain.enums.ExperienceStatus;
import org.example.explog.domain.enums.SourceType;
import org.example.explog.dto.request.ExperienceSaveRequest;
import org.example.explog.dto.response.*;
import org.example.explog.repository.ExperienceRepository;
import org.example.explog.repository.FolderRepository;
import org.example.explog.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // ⭐ 비동기 처리를 위한 서비스 주입
    private final AiAnalysisService aiAnalysisService;


    // 2번 기능: 경험 수기 작성 (비동기 AI 호출 포함)
    @Transactional
    public Long createExperience(ExperienceSaveRequest request) {
        User user = userRepository.findById(1L).orElseThrow();

        // 1. DB에 우선 저장 (초기 상태: ANALYZING)
        Experience experience = Experience.builder()
                .input(request.input())
                .content(request.input()) // 초기 내용은 input과 동일하게
                .user(user)
                .sourceType(SourceType.MANUAL)
                .sourceUrl(null)
                .build();

        Experience savedExperience = experienceRepository.save(experience);

        // 2. 비동기 AI 분석 요청 (결과를 기다리지 않고 실행만 시켜둠) 🚀
        aiAnalysisService.runAnalysis(savedExperience.getId(), request.input());

        // 3. ID 바로 반환 (프론트는 기다리지 않음)
        return savedExperience.getId();
    }

    public List<ExperienceSummaryDto> getRecentExperiences(Integer size) {
        // 1번 유저 하드코딩
        User user = userRepository.findById(1L).orElseThrow();

        List<Experience> experiences;

        if (size == null) {
            // [size가 없으면] -> 전체 조회
            experiences = experienceRepository.findAllByUserAndStatusNotOrderByCreatedAtDesc(user, ExperienceStatus.ANALYZING);
        } else {
            // Analyzing 상태 제외
            Pageable limit = PageRequest.of(0, size);
            experiences = experienceRepository.findByUserAndStatusNotOrderByCreatedAtDesc(user, ExperienceStatus.ANALYZING, limit);
        }

        return experiences.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
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
                experience.getContent(),
                experience.getStatus()
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
                experience.getContent(),
                experience.getCreatedAt(),
                experience.getStatus()
        );
    }
}