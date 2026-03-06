package com.floriano.legato_api.dto.ColaborationDTO;


import com.floriano.legato_api.model.User.enums.Genre;

import java.time.LocalDateTime;
import java.util.List;

public record ColaborationResponseDTO(
        Long id,
        String title,
        String author,
        String royalties,
        List<Genre> genres,
        boolean remote,
        LocalDateTime deadline,
        String imageUrl,
        LocalDateTime createdAt,
        String timeAgo,
        Long userId
) {}
