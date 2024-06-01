package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PagedRankResponseDto {
    private boolean hasNextPage;
    private List<RankingUserInfoRequestDto> data;

    @Builder
    public PagedRankResponseDto(boolean hasNextPage, List<RankingUserInfoRequestDto> data) {
        this.hasNextPage = hasNextPage;
        this.data = data;
    }
}
