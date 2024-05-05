package me.jejunu.opensource_supporter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GithubAuthWithdrawalRequestDto {
    private String access_token;
}
