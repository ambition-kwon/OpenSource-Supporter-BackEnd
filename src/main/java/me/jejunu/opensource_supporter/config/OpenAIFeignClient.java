package me.jejunu.opensource_supporter.config;

import me.jejunu.opensource_supporter.dto.AIRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "OpenAI", url = "https://api.openai.com")
public interface OpenAIFeignClient {
    @PostMapping(value = "/v1/chat/completions")
    String getChatGpt(@RequestBody AIRequestDto request, @RequestHeader("Authorization") String authorization);
}
