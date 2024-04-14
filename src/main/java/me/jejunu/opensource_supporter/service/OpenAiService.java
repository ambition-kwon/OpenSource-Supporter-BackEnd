package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.OpenAiFeignClient;
import me.jejunu.opensource_supporter.dto.ChatGptRequestDto;
import me.jejunu.opensource_supporter.dto.TranslateRequestDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {
    private final OpenAiFeignClient openAiFeignClient;

    public Object translateText(TranslateRequestDto request, String authorization){
        String model = "gpt-3.5-turbo";
        List<ChatGptRequestDto.ChatMessageDto> requestMessages = new ArrayList<>();
        requestMessages.add(new ChatGptRequestDto.ChatMessageDto("user", request.getText()));
        return openAiFeignClient.getChatGpt(new ChatGptRequestDto(model, requestMessages), authorization);
    }
}
