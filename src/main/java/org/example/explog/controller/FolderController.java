package org.example.explog.controller;

import org.example.explog.dto.FolderDto;
import org.example.explog.dto.FolderResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class FolderController {

    @GetMapping("/folders")
    public FolderResponseDto getFolders() {

        FolderDto folder1 = new FolderDto(
                1L,
                "DEVELOPMENT",
                "경험기록앱 개발",
                LocalDateTime.now()
        );

        FolderDto folder2 = new FolderDto(
                2L,
                "PART_TIME",
                "버거킹 알바",
                LocalDateTime.now()
        );

        return new FolderResponseDto(
                List.of(folder1, folder2)
        );
    }
}
