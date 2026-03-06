package com.floriano.legato_api.dto.ColaborationDTO;

import com.floriano.legato_api.model.User.enums.Genre;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Builder
public record ColaborationRequestDTO(
        String title,
        String royalties,
        List<Genre> genres,
        Boolean remote,
        LocalDateTime deadline
) {}