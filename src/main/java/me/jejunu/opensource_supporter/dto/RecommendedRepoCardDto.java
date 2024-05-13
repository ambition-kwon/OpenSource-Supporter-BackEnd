package me.jejunu.opensource_supporter.dto;

import lombok.*;
import me.jejunu.opensource_supporter.domain.RepoItem;

import java.util.List;

@Getter
@NoArgsConstructor
public class RecommendedRepoCardDto {
    private List<RepoItem> recentlyCommitRepoList;

    @Builder
    public RecommendedRepoCardDto(List<RepoItem> recentlyCommitRepoList) {
        this.recentlyCommitRepoList = recentlyCommitRepoList;
    }
}
