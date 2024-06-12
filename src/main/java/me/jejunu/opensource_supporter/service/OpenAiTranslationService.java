package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.OpenAiFeignClient;
import me.jejunu.opensource_supporter.dto.ChatGptRequestDto;
import me.jejunu.opensource_supporter.dto.TranslateRequestDto;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiTranslationService {
    private final OpenAiFeignClient openAiFeignClient;

    public String translateText(TranslateRequestDto request, String authorization){
        String model = "gpt-3.5-turbo";
        List<ChatGptRequestDto.ChatMessageDto> requestMessages = new ArrayList<>();
        String prompt = "특정 언어가 꼭 필요한 부분을 제외하고 다음글을 " + request.getLanguage() + "로 번역해줘 :" + request.getText();
        requestMessages.add(new ChatGptRequestDto.ChatMessageDto("user", prompt));
        JSONObject chatGpt = new JSONObject(openAiFeignClient.getChatGpt(new ChatGptRequestDto(model, requestMessages), authorization));
        return chatGpt.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}
