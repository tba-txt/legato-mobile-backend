package com.floriano.legato_api.dto.ChatDTO;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record ChatSummaryDTO(
        Long chatId,
        Long otherUserId,
        String otherUserName,
        String otherUserProfilePicture,
        String lastMessageContent,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime lastMessageTimestamp
) {}