package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.dto.TranslateRequestDto;
import me.jejunu.opensource_supporter.service.TranslationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class TranslationController {
    private final TranslationService translationService;

    @Value("${openai.api-key}")
    private String openApiKey;
    @Value("${groq.api-key}")
    private String groqApiKey;

    @PostMapping("/api/translate")
    public ResponseEntity<String> translateText(@RequestBody TranslateRequestDto request){
        String response = translationService.translateText(request, "Bearer " + openApiKey, "Bearer " + groqApiKey);
        return ResponseEntity.ok().body(response);
    }
}
