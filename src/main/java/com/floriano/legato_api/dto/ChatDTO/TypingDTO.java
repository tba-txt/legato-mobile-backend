package com.floriano.legato_api.dto.ChatDTO;

import lombok.Data;

@Data
public class TypingDTO {
    private Long chatId;
    private Long userId;
    private boolean isTyping;
}