package me.jejunu.opensource_supporter.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "GithubStats", url = "https://github-readme-stats.vercel.app")
public interface GithubStatsFeignClient {
    @GetMapping(value = "/api")
    String getUserAnalysis(@RequestParam("username") String username);
}
