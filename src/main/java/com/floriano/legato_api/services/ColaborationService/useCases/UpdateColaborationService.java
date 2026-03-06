package com.floriano.legato_api.services.ColaborationService.useCases;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationRequestDTO;
import com.floriano.legato_api.dto.ColaborationDTO.ColaborationResponseDTO;
import com.floriano.legato_api.dto.ColaborationDTO.ColaborationUpdateDTO;
import com.floriano.legato_api.mapper.ColaborationMapper.ColaborationMapper;
import com.floriano.legato_api.model.Colaboration.Colaboration;
import com.floriano.legato_api.repositories.ColaborationRepository;
import com.floriano.legato_api.services.ColaborationService.utils.ColaborationUpdateHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateColaborationService {

    private final ColaborationRepository colaborationRepository;

    public ColaborationResponseDTO execute(Long id, ColaborationUpdateDTO dto) {
        Colaboration colab = colaborationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colaboração com id " + id + " não encontrada"));

        ColaborationUpdateHelper.updateFields(colab, dto);

        colaborationRepository.save(colab);

        return ColaborationMapper.toResponse(colab);
    }
}

