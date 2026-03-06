package com.floriano.legato_api.dto.ColaborationDTO;

import com.floriano.legato_api.model.User.enums.Genre;

import java.time.LocalDateTime;
import java.util.List;

public record ColaborationUpdateDTO(
        String title,
        String royalties,
        List<Genre> genres,
        Boolean remote,
        LocalDateTime deadline
) {}
