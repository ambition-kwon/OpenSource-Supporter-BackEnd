package me.jejunu.opensource_supporter.config;

import me.jejunu.opensource_supporter.dto.AIRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "Groq", url = "https://api.groq.com/openai")
public interface GroqFeignClient {
    @PostMapping(value = "/v1/chat/completions", consumes = "application/json")
    String getGroq(@RequestBody AIRequestDto request, @RequestHeader("Authorization") String authorization);
}
