package com.floriano.legato_api.dto.ChatDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floriano.legato_api.model.ChatMessage.MessageStatus;

import java.time.LocalDateTime;

public record MessageStatusUpdateDTO(
        Long chatId,
        Long messageId,
        MessageStatus status,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime timestamp
) {}
