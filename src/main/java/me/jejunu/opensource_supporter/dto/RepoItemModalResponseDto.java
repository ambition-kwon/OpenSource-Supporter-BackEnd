package me.jejunu.opensource_supporter.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RepoItemModalResponseDto {
    private Long repoId;
    private String repoName;
    private Integer forkCount;
    private Integer starCount;
    private LocalDateTime lastCommitAt;
    private boolean posted;

    @Builder
    public RepoItemModalResponseDto(Long repoId, String repoName, Integer forkCount, Integer starCount, LocalDateTime lastCommitAt, boolean posted) {
        this.repoId = repoId;
        this.repoName = repoName;
        this.forkCount = forkCount;
        this.starCount = starCount;
        this.lastCommitAt = lastCommitAt;
        this.posted = posted;
    }
}
