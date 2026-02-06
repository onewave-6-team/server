package org.example.explog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderDto {
    private Long folderId;
    private String folderType;
    private String name;
    private LocalDateTime updatedAt;
}
