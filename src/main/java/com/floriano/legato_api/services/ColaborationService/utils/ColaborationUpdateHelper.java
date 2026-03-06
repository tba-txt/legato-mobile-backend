package com.floriano.legato_api.services.ColaborationService.utils;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationRequestDTO;
import com.floriano.legato_api.dto.ColaborationDTO.ColaborationUpdateDTO;
import com.floriano.legato_api.model.Colaboration.Colaboration;

public class ColaborationUpdateHelper {

    public static void updateFields(Colaboration colab, ColaborationUpdateDTO dto) {

        if (dto.title() != null)
            colab.setTitle(dto.title());

        if (dto.royalties() != null)
            colab.setRoyalties(dto.royalties());

        if (dto.genres() != null)
            colab.setGenres(dto.genres());

        if (dto.deadline() != null)
            colab.setDeadline(dto.deadline());

        if (dto.remote() != null)
            colab.setRemote(dto.remote());

    }
}