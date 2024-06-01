package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PagedRepoItemResponseDto {
    private boolean hasNextPage;
    private List<RecommendedRepoCardDto> data;

    @Builder
    public PagedRepoItemResponseDto(boolean hasNextPage, List<RecommendedRepoCardDto> data) {
        this.hasNextPage = hasNextPage;
        this.data = data;
    }
}
