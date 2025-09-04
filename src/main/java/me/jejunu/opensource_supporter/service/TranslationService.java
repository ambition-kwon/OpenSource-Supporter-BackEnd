package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GroqFeignClient;
import me.jejunu.opensource_supporter.config.OpenAIFeignClient;
import me.jejunu.opensource_supporter.dto.AIRequestDto;
import me.jejunu.opensource_supporter.dto.TranslateRequestDto;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final GroqFeignClient groqFeignClient;
    private final ConcurrentHashMap<String, String> translationCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService;
    private final OpenAIFeignClient openAIFeignClient;

    public String translateText(TranslateRequestDto request, String openApiKey, String groqApiKey){
        String cacheKey = request.getLanguage() + "-" + request.getText();
        String translation = translationCache.get(cacheKey);
        if(translation == null){
            List<AIRequestDto.ChatMessageDto> requestMessages = new ArrayList<>();
            String prompt = generatePrompt(request);
            requestMessages.add(new AIRequestDto.ChatMessageDto("user", prompt));
            JSONObject response = getAIResponse("groq", requestMessages, openApiKey, groqApiKey);
            translation = response.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            translationCache.put(cacheKey, translation);
            scheduledExecutorService.schedule(() -> translationCache.remove(cacheKey), 10, TimeUnit.MINUTES);
        }
        return translation;
    }

    private JSONObject getAIResponse(String aiModel, List<AIRequestDto.ChatMessageDto> requestMessages, String openApiKey, String groqApiKey) {
        if (aiModel.equals("chatgpt")) {
            return new JSONObject(openAIFeignClient.getChatGpt(new AIRequestDto("gpt-5-mini", requestMessages), openApiKey));
        }
        else if(aiModel.equals("groq")){
            return new JSONObject(groqFeignClient.getGroq(new AIRequestDto("Gemma2-9b-it", requestMessages), groqApiKey));
        }
        else return null;
    }

    private String generatePrompt(TranslateRequestDto request) {
        return "특정 언어가 꼭 필요한 부분을 제외하고 다음글을 " + request.getLanguage() + "로 번역해줘. 이 때 만약 내가 준 내용의 언어와 내가 요청하는 언어가 동일할 경우 내가 줬던 내용을 절대 수정 및 삭제 하지말고 그대로 다시 출력해줘. 번역할 내용은 다음과 같아 : " + request.getText();
    }
}
