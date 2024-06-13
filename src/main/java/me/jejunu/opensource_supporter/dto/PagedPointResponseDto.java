package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PagedPointResponseDto {
    private boolean hasNextPage;
    private List<PointResponseDto> data;

    @Builder
    public PagedPointResponseDto(boolean hasNextPage, List<PointResponseDto> data) {
        this.hasNextPage = hasNextPage;
        this.data = data;
    }
}
