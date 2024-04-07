package me.jejunu.opensource_supporter.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "GithubApi", url = "https://api.github.com")
public interface GithubApiFeignClient {
    @GetMapping(value = "/user",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String getUser(@RequestHeader("Authorization") String authorization);
}
