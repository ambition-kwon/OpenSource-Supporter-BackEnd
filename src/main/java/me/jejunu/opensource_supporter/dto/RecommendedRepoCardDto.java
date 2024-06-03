package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecommendedRepoCardDto {
    private Long id;
    private String userName;
    private String avatarUrl;
    private String repoName;
    private String description;
    private List<String> tags;
    private String mostLanguage;
    private String license;
    private String repositoryLink;
    private int viewCount;
    private int totalPoint;
    private LocalDateTime lastCommitAt;

    @Builder
    public RecommendedRepoCardDto(Long id, String userName, String avatarUrl, String repoName, String description, List<String> tags, String mostLanguage, String license, String repositoryLink, int viewCount, int totalPoint, LocalDateTime lastCommitAt) {
        this.id = id;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.repoName = repoName;
        this.description = description;
        this.tags = tags;
        this.mostLanguage = mostLanguage;
        this.license = license;
        this.repositoryLink = repositoryLink;
        this.viewCount = viewCount;
        this.totalPoint = totalPoint;
        this.lastCommitAt = lastCommitAt;
    }
}
