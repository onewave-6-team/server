package org.example.explog.dto;

import java.time.LocalDateTime;

public class FolderDto {

    private Long folderId;
    private String folderType;
    private String name;
    private LocalDateTime updatedAt;

    public FolderDto(Long folderId, String folderType, String name, LocalDateTime updatedAt) {
        this.folderId = folderId;
        this.folderType = folderType;
        this.name = name;
        this.updatedAt = updatedAt;
    }

    public Long getFolderId() {
        return folderId;
    }

    public String getFolderType() {
        return folderType;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
