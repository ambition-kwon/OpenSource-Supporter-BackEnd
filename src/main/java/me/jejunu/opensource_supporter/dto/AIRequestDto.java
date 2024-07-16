package me.jejunu.opensource_supporter.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AIRequestDto {
    private String model;
    private List<ChatMessageDto> messages;

    public AIRequestDto(String model, List<ChatMessageDto> messages) {
        this.model = model;
        this.messages = messages;
    }

    @NoArgsConstructor
    @Getter
    public static class ChatMessageDto {
        private String role;
        private String content;

        public ChatMessageDto(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
