package me.jejunu.opensource_supporter.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "GithubLogin", url = "https://github.com")
public interface GithubLoginFeignClient {
    @PostMapping(value = "/login/oauth/access_token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String getAcessToken(@RequestParam("client_id") String clientId,
                         @RequestParam("client_secret") String clientSecret,
                         @RequestParam("code") String code);
}

