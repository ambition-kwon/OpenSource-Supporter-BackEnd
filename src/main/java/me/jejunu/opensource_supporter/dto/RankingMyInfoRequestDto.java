package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RankingMyInfoRequestDto {
    private int rank;
    private String userName;
    private String customName;
    private String avatarUrl;
    private int usedPoint;
    private double topPercent;

    @Builder

    public RankingMyInfoRequestDto(int rank, String userName, String customName, String avatarUrl, int usedPoint, double topPercent) {
        this.rank = rank;
        this.userName = userName;
        this.customName = customName;
        this.avatarUrl = avatarUrl;
        this.usedPoint = usedPoint;
        this.topPercent = topPercent;
    }
}
