package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class RepoItemDetailResponseDto {
    private String avatarUrl;
    private String userName;
    private List<String> tags;
    private String mostLanguage;
    private String license;
    private String repositoryLink;
    private LocalDateTime lastCommitAt;
    private int viewCount;
    private String description;
    private String readmeContent;
    private List<Integer> weeklyCommitList;
    private int totalCommits;
    private int totalStars;
    private int totalPullRequests;
    private int totalIssues;
    private int totalContributions;
    private String rank;
    private String chatgptAnalysis;

    @Builder
    public RepoItemDetailResponseDto(String avatarUrl, String userName, List<String> tags, String mostLanguage, String license, String repositoryLink, LocalDateTime lastCommitAt, int viewCount, String description, String readmeContent, List<Integer> weeklyCommitList, int totalCommits, int totalStars, int totalPullRequests, int totalIssues, int totalContributions, String rank, String chatgptAnalysis) {
        this.avatarUrl = avatarUrl;
        this.userName = userName;
        this.tags = tags;
        this.mostLanguage = mostLanguage;
        this.license = license;
        this.repositoryLink = repositoryLink;
        this.lastCommitAt = lastCommitAt;
        this.viewCount = viewCount;
        this.description = description;
        this.readmeContent = readmeContent;
        this.weeklyCommitList = weeklyCommitList;
        this.totalCommits = totalCommits;
        this.totalStars = totalStars;
        this.totalPullRequests = totalPullRequests;
        this.totalIssues = totalIssues;
        this.totalContributions = totalContributions;
        this.rank = rank;
        this.chatgptAnalysis = chatgptAnalysis;
    }
}
