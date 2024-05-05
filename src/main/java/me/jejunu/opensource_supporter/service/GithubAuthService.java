package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubApiFeignClient;
import me.jejunu.opensource_supporter.config.GithubAuthFeignClient;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.GithubAuthWithdrawalRequestDto;
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
        Object response = githubAuthFeignClient.getAcessToken(clientId, clientSecret, code);
        JSONObject jsonObject = new JSONObject(response.toString());
        if(jsonObject.has("error")){
            throw new RuntimeException("GitHub 엑세스 토큰 요청 실패 : " + jsonObject.getString("error"));
        } else {
            return jsonObject.getString("access_token");
        }
    }

    public Optional<User> signupOrLogin(String userName){
        Optional<User> user = userRepository.findByUserName(userName);
        if(user.isPresent()){
            System.out.println("있는 유저입니다.");
            return user;
        } else {
            System.out.println("최초 가입입니다.");
            User newUser = userRepository.save(User.builder()
                    .userName(userName)
                    .build()
            );
            return Optional.of(newUser);
        }
    }

    @Transactional
    public void accountTermination(String clientId, String clientSecret, GithubAuthWithdrawalRequestDto request){
        String adminAuth = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        JSONObject userDataResponse = githubApiService.getUserFromGithub(request.getAccess_token());
        String userName = userDataResponse.getString("login");
        githubApiFeignClient.accountTermination(clientId, adminAuth, request);
        System.out.println("Github OAuth 회원탈퇴 완료");
        userRepository.deleteByUserName(userName);
        System.out.println("DB 에서 회원삭제 완료");
    }
}
