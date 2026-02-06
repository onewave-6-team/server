package org.example.explog.dto;

import java.time.LocalDateTime;

public class ExperienceDto {

    private Long experienceId;
    private String experienceType;
    private String sourceType;
    private String sourceUrl;
    private String title;
    private String summary;
    private LocalDateTime createdAt;

    public ExperienceDto() {
    }

    public ExperienceDto(Long experienceId, String experienceType, String sourceType,
                         String sourceUrl, String title, String summary, LocalDateTime createdAt) {
        this.experienceId = experienceId;
        this.experienceType = experienceType;
        this.sourceType = sourceType;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    public Long getExperienceId() {
        return experienceId;
    }

    public String getExperienceType() {
        return experienceType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
