package me.jejunu.opensource_supporter.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatGptRequestDto {
    private String model;
    private List<ChatMessageDto> messages;

    public ChatGptRequestDto(String model, List<ChatMessageDto> messages) {
        this.model = model;
        this.messages = messages;
    }

    @NoArgsConstructor
    @Getter
    public static class ChatMessageDto {
        private String role;
        private String content;

        public ChatMessageDto(String role, String content) {
            this.role = "user";
            this.content = content;
        }
    }
}
