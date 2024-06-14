package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.dto.TranslateRequestDto;
import me.jejunu.opensource_supporter.service.OpenAiTranslationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class OpenAiTranslationController {
    private final OpenAiTranslationService openAiTranslationService;

    @Value("${openai.api-key}")
    private String openApiKey;

    @GetMapping("/api/translate")
    public ResponseEntity<String> translateText(@RequestBody TranslateRequestDto request){
        String response = openAiTranslationService.translateText(request, "Bearer " + openApiKey);
        return ResponseEntity.ok().body(response);
    }
}
