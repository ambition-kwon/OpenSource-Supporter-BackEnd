package me.jejunu.opensource_supporter.config;

import me.jejunu.opensource_supporter.dto.GithubAuthWithdrawalRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "GithubApi", url = "https://api.github.com")
public interface GithubApiFeignClient {
    @GetMapping(value = "/user")
    String getUser(@RequestHeader("Authorization") String authorization);

    @DeleteMapping(value = "/applications/{clientId}/grant")
    void accountTermination(
            @PathVariable("clientId") String clientId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody GithubAuthWithdrawalRequestDto request
    );
}
