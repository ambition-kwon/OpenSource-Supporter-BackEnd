package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubAuthFeignClient;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GithubAuthService {
    private final GithubAuthFeignClient githubAuthFeignClient;
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
            return user;
        } else {
            User newUser = userRepository.save(User.builder()
                    .userName(userName)
                    .build()
            );
            return Optional.of(newUser);
        }
    }
}
