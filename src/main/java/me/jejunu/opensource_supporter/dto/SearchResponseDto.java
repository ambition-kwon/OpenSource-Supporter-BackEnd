package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jejunu.opensource_supporter.domain.User;

import java.util.List;

@NoArgsConstructor
@Getter
public class SearchResponseDto {
    private List<User> users;
    private List<RecommendedRepoCardDto> repositories;

    @Builder
    public SearchResponseDto(List<User> users, List<RecommendedRepoCardDto> repositories) {
        this.users = users;
        this.repositories = repositories;
    }
}
