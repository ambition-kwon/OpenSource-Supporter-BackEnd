package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubApiFeignClient;
import me.jejunu.opensource_supporter.config.GithubLoginFeignClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final GithubLoginFeignClient githubLoginFeignClient;
    private final GithubApiFeignClient githubApiFeignClient;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;
    private String codeUrl = "https://github.com/login/oauth/authorize";

    @GetMapping("/api/auth/login")
    public RedirectView redirectGithub(){
        String url = codeUrl + "?client_id=" + clientId;
        return new RedirectView(url);
    }

    @GetMapping("/api/auth/login/response")
    public ResponseEntity<String> handleResponse(@RequestParam("code") String code) {
        Object response = githubLoginFeignClient.getAcessToken(clientId, clientSecret, code);
        System.out.println("response = " + response);
        JSONObject jsonObject = new JSONObject(response.toString());
        String access_token = jsonObject.getString("access_token");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("access_token", access_token);
        //TODO: user DB 구현(있으면 불러오기, 없으면 추가)
        Object user = githubApiFeignClient.getUser("Bearer " + access_token);
        System.out.println("user = " + user);
        return ResponseEntity.ok().body(user.toString());
    }
}
