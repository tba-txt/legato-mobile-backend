package com.floriano.legato_api.dto.ChatDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record UserPresenceDTO(
        Long userId,
        Boolean isOnline,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime lastSeen
) {}
