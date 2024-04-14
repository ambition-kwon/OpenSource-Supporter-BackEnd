package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GithubAuthResponseDto {
    private String userName;
    private String customName;
    private String email;
    private String avatarUrl;

    @Builder
    public GithubAuthResponseDto(String userName, String customName, String email, String avatarUrl) {
        this.userName = userName;
        this.customName = customName;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }
}
