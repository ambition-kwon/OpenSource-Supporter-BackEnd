package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GithubAuthLoginResponseDto {
    private String userName;
    private String customName;
    private String email;
    private String avatarUrl;
    private String accessToken;

    @Builder
    public GithubAuthLoginResponseDto(String userName, String customName, String email, String avatarUrl, String accessToken) {
        this.userName = userName;
        this.customName = customName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.accessToken = accessToken;
    }
}
