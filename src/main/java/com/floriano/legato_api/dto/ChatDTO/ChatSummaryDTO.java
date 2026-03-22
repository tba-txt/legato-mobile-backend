package com.floriano.legato_api.dto.ChatDTO;

import java.time.LocalDateTime;

public record ChatSummaryDTO(
        Long chatId,
        Long otherUserId,
        String otherUserName,
        String otherUserProfilePicture,
        String lastMessageContent,
        LocalDateTime lastMessageTimestamp
) {}