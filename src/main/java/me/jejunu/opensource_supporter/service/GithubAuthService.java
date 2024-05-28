package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubApiFeignClient;
import me.jejunu.opensource_supporter.config.GithubAuthFeignClient;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.GithubTokenDto;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GithubAuthService {
    private final GithubAuthFeignClient githubAuthFeignClient;
    private final GithubApiFeignClient githubApiFeignClient;
    private final GithubApiService githubApiService;
    private final UserRepository userRepository;

    public String getAccessTokenFromGithub(String clientId, String clientSecret, String code) {
        Object response = githubAuthFeignClient.getAccessToken(clientId, clientSecret, code);
        JSONObject jsonObject = new JSONObject(response.toString());
        if(jsonObject.has("error")){
            throw new RuntimeException("GitHub 엑세스 토큰 요청 실패 : " + jsonObject.getString("error"));
        } else {
            return jsonObject.getString("access_token");
        }
    }

    public Optional<User> signupOrLogin(String userName, String avatarUrl, String customName){
        Optional<User> user = userRepository.findByUserName(userName);
        if(user.isPresent()){
            if(!user.get().getCustomName().equals(customName)) user.get().setCustomName(customName);
            if(!user.get().getAvatarUrl().equals(avatarUrl)) user.get().setAvatarUrl(avatarUrl);
            System.out.println("있는 유저입니다.");
            return user;
        } else {
            User newUser = userRepository.save(User.builder()
                    .userName(userName)
                    .avatarUrl(avatarUrl)
                    .customName(customName)
                    .build()
            );
            System.out.println("최초 가입입니다.");
            return Optional.of(newUser);
        }
    }

    @Transactional
    public void accountTermination(String clientId, String clientSecret, String authHeader){
        String adminAuth = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        githubApiFeignClient.accountTermination(clientId, adminAuth, new GithubTokenDto(userToken));
        System.out.println("Github OAuth 회원탈퇴 완료");
        userRepository.deleteByUserName(userName);
        System.out.println("DB 에서 회원삭제 완료");
    }

    public void tokenTermination(String clientId, String clientSecret, String authHeader){
        String adminAuth = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        String userToken = authHeader.replace("Bearer ", "");
        githubApiFeignClient.tokenTermination(clientId, adminAuth, new GithubTokenDto(userToken));
        System.out.println("Github Access_Token 삭제 완료 / 로그아웃 완료");
    }
}
