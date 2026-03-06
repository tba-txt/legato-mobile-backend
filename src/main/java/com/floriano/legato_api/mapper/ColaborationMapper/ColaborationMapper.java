package com.floriano.legato_api.mapper.ColaborationMapper;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationRequestDTO;
import com.floriano.legato_api.dto.ColaborationDTO.ColaborationResponseDTO;
import com.floriano.legato_api.model.Colaboration.Colaboration;


public class ColaborationMapper {

    public static Colaboration toEntity(ColaborationRequestDTO dto) {

        Colaboration colab = new Colaboration();

        colab.setTitle(dto.title());
        colab.setRoyalties(dto.royalties());
        colab.setGenres(dto.genres());
        colab.setRemote(Boolean.TRUE.equals(dto.remote()));
        colab.setDeadline(dto.deadline());

        return colab;
    }

    public static ColaborationResponseDTO toResponse(Colaboration colab) {
        return new ColaborationResponseDTO(
                colab.getId(),
                colab.getTitle(),
                colab.getAuthor(),
                colab.getRoyalties(),
                colab.getGenres(),
                colab.isRemote(),
                colab.getDeadline(),
                colab.getImageUrl(),
                colab.getCreatedAt(),
                colab.getTimeAgo(),
                colab.getUser().getId()
        );
    }
}
