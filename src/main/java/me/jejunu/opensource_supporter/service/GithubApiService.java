package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubApiFeignClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubApiService {
    private final GithubApiFeignClient githubApiFeignClient;

    public JSONObject getUserFromGithub(String access_token){
        Object response = githubApiFeignClient.getUser("Bearer " + access_token);
        return new JSONObject(response.toString());
    }
}
