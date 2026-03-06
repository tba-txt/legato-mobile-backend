package com.floriano.legato_api.dto.ChatDTO;

import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private String senderName;
    private String senderEmail;

    public static ChatMessageDTO from(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .senderName(message.getSender() != null ? message.getSender().getUsername() : "Desconhecido")
                .senderEmail(message.getSender() != null ? message.getSender().getEmail() : "?")
                .build();
    }
}

