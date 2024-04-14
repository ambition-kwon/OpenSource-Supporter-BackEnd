package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.dto.TranslateRequestDto;
import me.jejunu.opensource_supporter.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OpenAiTranslationController {
    private final OpenAiService openAiService;

    @Value("${openai.api-key}")
    private String apiKey;

    @GetMapping("/api/translate")
    public ResponseEntity<Object> translateText(@RequestBody TranslateRequestDto request){
        String authorization = "Bearer " + apiKey;
        Object response = openAiService.translateText(request, authorization);
        return ResponseEntity.ok().body(response);
    }
}
