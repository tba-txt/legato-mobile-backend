package com.floriano.legato_api.dto.ChatDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import com.floriano.legato_api.model.ChatMessage.MessageStatus;
import com.floriano.legato_api.model.Post.TypeMedia; // IMPORTANTE
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

    private MessageStatus status;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime readAt;

    // --- CAMPOS DE MÍDIA ---
    private TypeMedia typeMedia;
    private String mediaUrl;
    private Long repliedMessageId;
    private String repliedMessageContent;
    private String repliedMessageSenderName;

    public static ChatMessageDTO from(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .senderName(message.getSender() != null ? message.getSender().getUsername() : "Desconhecido")
                .senderEmail(message.getSender() != null ? message.getSender().getEmail() : "?")
                .status(message.getStatus() != null ? message.getStatus() : MessageStatus.SENT)
                .readAt(message.getReadAt())
                .typeMedia(message.getTypeMedia() != null ? message.getTypeMedia() : TypeMedia.NONE)
                .mediaUrl(message.getMediaUrl())
                .repliedMessageId(message.getRepliedMessage() != null ? message.getRepliedMessage().getId() : null)
                .repliedMessageContent(message.getRepliedMessage() != null ? message.getRepliedMessage().getContent() : null)
                .repliedMessageSenderName(message.getRepliedMessage() != null && message.getRepliedMessage().getSender() != null 
                        ? message.getRepliedMessage().getSender().getUsername() : null)
                .build();
    }
}