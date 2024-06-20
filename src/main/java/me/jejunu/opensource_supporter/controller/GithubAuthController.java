package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.RecommendedRepoItemScheduling;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.GithubAuthLoginResponseDto;
import me.jejunu.opensource_supporter.service.GithubApiService;
import me.jejunu.opensource_supporter.service.GithubAuthService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@CrossOrigin
public class GithubAuthController {

    private final GithubAuthService githubAuthService;
    private final GithubApiService githubApiService;
    private final RecommendedRepoItemScheduling recommendedRepoItemScheduling;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    //BackEnd 테스트용 페이지 리다이렉션 링크
    @GetMapping("/api/auth/login/page")
    public RedirectView redirectToGithubAuthPage(){
        String codeUrl = "https://github.com/login/oauth/authorize?client_id=";
        String url = codeUrl + clientId;
        return new RedirectView(url);
    }

    // http://localhost:5173/github-auth -> FrontEnd GitHub response endpoint
    @GetMapping("/api/auth/login")
    public ResponseEntity<Object> handleGithubLoginResponse(@RequestParam("code") String code) {
        String access_token = githubAuthService.getAccessTokenFromGithub(clientId, clientSecret, code);
        System.out.println("`access_token` = " + access_token);
        JSONObject userDataResponse = githubApiService.getUserFromGithub(access_token);
        String userName = userDataResponse.getString("login");
        String customName = userDataResponse.optString("name", "No name");
        String avatarUrl = userDataResponse.optString("avatar_url", null);
        User user = githubAuthService.signupOrLogin(userName, avatarUrl, customName)
                .orElseThrow(()->new IllegalArgumentException("user load failed"));

        return ResponseEntity.ok().body(GithubAuthLoginResponseDto.builder()
                .userName(userName)
                .customName(user.getCustomName())
                .email(userDataResponse.optString("email", null))
                .avatarUrl(user.getAvatarUrl())
                .accessToken(access_token)
                .remainingPoint(user.getRemainingPoint())
                .totalPoint(user.getTotalPoint())
                .adLink(user.getAdLink())
                .cardLink(user.getCardLink())
                .build());
    }

    @GetMapping("/api/refresh")
    public ResponseEntity<User> getUserInfoByAccessToken(@RequestHeader("Authorization") String authHeader){
        User user = githubAuthService.getUserInfoByAccessToken(authHeader);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/api/auth/logout")
    public ResponseEntity<Void> handleGithubAccountLogout(@RequestHeader("Authorization") String authHeader){
        githubAuthService.tokenTermination(clientId, clientSecret, authHeader);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/auth/withdrawal")
    public ResponseEntity<Void> handleGithubAccountTermination(@RequestHeader("Authorization") String authHeader){
        githubAuthService.accountTermination(clientId, clientSecret, authHeader);
        return ResponseEntity.ok().build();
    }
}
