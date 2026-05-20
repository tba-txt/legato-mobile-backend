package com.floriano.legato_api.dto.ChatDTO;

public record ChatMessageRequestDTO(
    Long receiverId,
    String content,
    Long repliedMessageId
) {}