package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RankingUserInfoRequestDto {
    private int rank;
    private String userName;
    private String customName;
    private String avatarUrl;
    private int usedPoint;


    @Builder
    public RankingUserInfoRequestDto(int rank, String userName, String customName, String avatarUrl, int usedPoint) {
        this.rank = rank;
        this.userName = userName;
        this.customName = customName;
        this.avatarUrl = avatarUrl;
        this.usedPoint = usedPoint;
    }
}
