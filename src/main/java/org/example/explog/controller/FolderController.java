package org.example.explog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.explog.dto.response.FolderListResponse;
import org.example.explog.service.ExperienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Folder", description = "폴더 관련 API")
@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final ExperienceService experienceService;

    @Operation(summary = "폴더 리스트 조회")
    @GetMapping
    public ResponseEntity<FolderListResponse> getFolders() {
        return ResponseEntity.ok(FolderListResponse.from(experienceService.getFolders()));
    }
}