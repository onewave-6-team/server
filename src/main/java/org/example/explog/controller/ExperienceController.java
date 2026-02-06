package org.example.explog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.explog.dto.request.ExperienceSaveRequest;
import org.example.explog.dto.response.ExperienceDetailResponse;
import org.example.explog.dto.response.ExperienceListResponse;
import org.example.explog.dto.response.ExperienceSummaryDto;
import org.example.explog.service.ExperienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Experience", description = "경험 기록 관련 API")
@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @Operation(summary = "최근 경험 리스트 조회 (Top 5)")
    @GetMapping("/recent")
    public ResponseEntity<ExperienceListResponse> getRecentExperiences(
            @RequestParam(defaultValue = "5") int size // 현재 로직은 무조건 5개지만, 확장성을 위해 남겨둠
    ) {
        List<ExperienceSummaryDto> dtos = experienceService.getRecentExperiences();
        return ResponseEntity.ok(ExperienceListResponse.from(dtos));
    }

    @Operation(summary = "경험 수기 작성")
    @PostMapping
    public ResponseEntity<Void> createExperience(@RequestBody @Valid ExperienceSaveRequest request) {
        experienceService.createExperience(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "폴더별 경험 리스트 조회")
    @GetMapping
    public ResponseEntity<ExperienceListResponse> getExperiencesByFolder(@RequestParam Long folderId) {
        List<ExperienceSummaryDto> dtos = experienceService.getExperiencesByFolder(folderId);
        return ResponseEntity.ok(ExperienceListResponse.from(dtos));
    }

    @Operation(summary = "경험 상세 조회")
    @GetMapping("/{experienceId}")
    public ResponseEntity<ExperienceDetailResponse> getExperienceDetail(@PathVariable Long experienceId) {
        return ResponseEntity.ok(experienceService.getExperienceDetail(experienceId));
    }
}