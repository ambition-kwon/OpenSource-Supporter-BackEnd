package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.GithubAuthResponseDto;
import me.jejunu.opensource_supporter.service.GithubApiService;
import me.jejunu.opensource_supporter.service.GithubAuthService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class GithubAuthController {

    private final GithubAuthService githubAuthService;
    private final GithubApiService githubApiService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @GetMapping("/api/auth/login")
    public RedirectView redirectToGithubAuthPage(){
        String codeUrl = "https://github.com/login/oauth/authorize?client_id=";
        String url = codeUrl + clientId;
        return new RedirectView(url);
    }

    @GetMapping("/api/auth/login/response")
    public ResponseEntity<GithubAuthResponseDto> handleGithubLoginResponse(@RequestParam("code") String code) {
        String access_token = githubAuthService.getAccessTokenFromGithub(clientId, clientSecret, code);
        JSONObject userDataResponse = githubApiService.getUserFromGithub(access_token);
        String userName = userDataResponse.getString("login");
        User user = githubAuthService.signupOrLogin(userName)
                .orElseThrow(()->new IllegalArgumentException("user load failed"));
        return ResponseEntity.ok().body(GithubAuthResponseDto.builder()
                .userName(userName)
                .customName(userDataResponse.optString("name", null))
                .email(userDataResponse.optString("email", null))
                .avatarUrl(userDataResponse.optString("avatar_url", null))
                .build()
        );
    }
}
