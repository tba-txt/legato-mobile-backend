package com.floriano.legato_api.dto.ChatDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import com.floriano.legato_api.model.ChatMessage.MessageStatus; 
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageDTO {
    private Long id;
    private String content;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime timestamp;
    
    private String senderName;
    private String senderEmail;

    // --- NOVOS CAMPOS ---
    private MessageStatus status;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime readAt;

    public static ChatMessageDTO from(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .senderName(message.getSender() != null ? message.getSender().getUsername() : "Desconhecido")
                .senderEmail(message.getSender() != null ? message.getSender().getEmail() : "?")
                .status(message.getStatus() != null ? message.getStatus() : MessageStatus.SENT)
                .readAt(message.getReadAt())
                .build();
    }
}