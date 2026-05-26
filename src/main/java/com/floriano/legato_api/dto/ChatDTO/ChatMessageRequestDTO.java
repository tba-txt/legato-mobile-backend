package com.floriano.legato_api.dto.ChatDTO;

import com.floriano.legato_api.model.Post.TypeMedia;

public record ChatMessageRequestDTO(
    Long receiverId,
    String content,
    Long repliedMessageId,
    TypeMedia typeMedia,
    String mediaUrl
) {}