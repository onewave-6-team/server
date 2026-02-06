package org.example.explog.domain;

import org.example.explog.domain.enums.CategoryType;
import org.example.explog.domain.enums.ExperienceStatus;
import org.example.explog.domain.enums.SourceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "experiences")
public class Experience extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String input;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType sourceType;

    @Column(columnDefinition = "TEXT")
    private String sourceUrl;
    
    @Enumerated(EnumType.STRING)
    private CategoryType category; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Builder
    public Experience(String input, SourceType sourceType, String sourceUrl, User user) {
        this.input = input;
        this.sourceType = sourceType;
        this.sourceUrl = sourceUrl;
        this.user = user;
        this.status = ExperienceStatus.ANALYZING;
    }
    
    public void updateStatus(ExperienceStatus status) {
        this.status = status;
    }
    
    public void updateContent(String title, String content, String summary, CategoryType category) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.category = category;
    }
}