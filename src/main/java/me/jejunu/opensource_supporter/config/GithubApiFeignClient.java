package me.jejunu.opensource_supporter.config;

import me.jejunu.opensource_supporter.dto.GithubAuthWithdrawalRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "GithubApi", url = "https://api.github.com")
public interface GithubApiFeignClient {
    @GetMapping(value = "/user",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String getUser(@RequestHeader("Authorization") String authorization);

    @DeleteMapping(value = "/applications/{clientId}/grant")
    void accountTermination(
            @PathVariable("clientId") String clientId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody GithubAuthWithdrawalRequestDto request
    );



    // 모스트 랭귀지
    @GetMapping(value = "/repos/{owner}/{repo}/languages",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String getMostLanguage(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestHeader("Authorization") String authorization
    );

    // 라이센스
    @GetMapping(value = "/repos/{owner}/{repo}/license"
            ,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            ,produces = MediaType.APPLICATION_JSON_VALUE)
    String getRepoLicense(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestHeader("Authorization") String authorization
    );

    // last commit
    @GetMapping(value = "/repos/{owner}/{repo}"
            , consumes =  MediaType.APPLICATION_FORM_URLENCODED_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    String getCommitSha(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestHeader("Authorication") String authorization
    );
}
