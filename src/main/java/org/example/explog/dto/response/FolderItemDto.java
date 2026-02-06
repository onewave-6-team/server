package org.example.explog.dto.response;

import org.example.explog.domain.enums.CategoryType;
import java.time.LocalDateTime;

public record FolderItemDto(
        Long folderId,
        CategoryType folderType,
        String name,
        LocalDateTime updatedAt
) {}