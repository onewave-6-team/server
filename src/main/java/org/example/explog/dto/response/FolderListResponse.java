package org.example.explog.dto.response;

import java.util.List;

public record FolderListResponse(
        List<FolderItemDto> folders
) {
    public static FolderListResponse from(List<FolderItemDto> folders) {
        return new FolderListResponse(folders);
    }
}