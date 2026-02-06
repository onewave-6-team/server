package org.example.explog.dto;

import java.util.List;

public class FolderResponseDto {

    private List<FolderDto> folders;

    public FolderResponseDto(List<FolderDto> folders) {
        this.folders = folders;
    }

    public List<FolderDto> getFolders() {
        return folders;
    }
}
