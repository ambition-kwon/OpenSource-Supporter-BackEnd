package me.jejunu.opensource_supporter.config;

import me.jejunu.opensource_supporter.dto.GithubTokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "GithubApi", url = "https://api.github.com")
public interface GithubApiFeignClient {
    @GetMapping(value = "/user",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String getUser(@RequestHeader("Authorization") String authorization);

    @GetMapping(value = "/users/{userName}/repos",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String getUserRepoItem(@RequestHeader("Authorization") String authorization,
                           @PathVariable("userName") String userName,
                           @RequestParam("sort") String sortRule,
                           @RequestParam("per_page") Integer perPageRule);

    @GetMapping(value ="/repos/{owner}/{repo}"
            ,consumes = MediaType.APPLICATION_JSON_VALUE
            ,produces = MediaType.APPLICATION_JSON_VALUE)
    String getUserSingleRepoItem(@RequestHeader("Authorization") String authorization,
                                 @PathVariable("owner") String owner,
                                 @PathVariable("repo") String repo);

    @DeleteMapping(value = "/applications/{clientId}/grant")
    void accountTermination(
            @PathVariable("clientId") String clientId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody GithubTokenDto request
    );

    @DeleteMapping(value = "/applications/{clientId}/token")
    void tokenTermination(
            @PathVariable("clientId") String clientId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody GithubTokenDto request
    );

    @GetMapping(value = "/repos/{owner}/{repo}/languages",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String getMostLanguage(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping(value = "/repos/{owner}/{repo}/license"
            ,consumes = MediaType.APPLICATION_JSON_VALUE
            ,produces = MediaType.APPLICATION_JSON_VALUE)
    String getRepoLicense(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping(value = "/repos/{owner}/{repo}"
            , consumes =  MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    String getCommitSha(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping(value = "/repos/{owner}/{repo}/readme"
            , consumes =  MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    String getReadme(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping(value = "/repos/{owner}/{repo}/stats/participation"
            , consumes =  MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    String getWeeklyCommitList(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestHeader("Authorization") String authorization
    );
}
