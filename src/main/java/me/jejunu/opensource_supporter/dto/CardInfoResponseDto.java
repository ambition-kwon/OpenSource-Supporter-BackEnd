package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CardInfoResponseDto {
    private String userName;
    private int totalDonated;
    private List<String> donatedRepoList;

    @Builder
    public CardInfoResponseDto(String userName, int totalDonated, List<String> donatedRepoList) {
        this.userName = userName;
        this.totalDonated = totalDonated;
        this.donatedRepoList = donatedRepoList;
    }
}
