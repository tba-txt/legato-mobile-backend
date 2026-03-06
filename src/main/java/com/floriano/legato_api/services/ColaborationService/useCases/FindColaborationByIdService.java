package com.floriano.legato_api.services.ColaborationService.useCases;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationResponseDTO;
import com.floriano.legato_api.mapper.ColaborationMapper.ColaborationMapper;
import com.floriano.legato_api.model.Colaboration.Colaboration;
import com.floriano.legato_api.repositories.ColaborationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindColaborationByIdService {

    private final ColaborationRepository colaborationRepository;

    public ColaborationResponseDTO execute(Long id) {
        Colaboration colab = colaborationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colaboração com id " + id + " não encontrada"));

        return ColaborationMapper.toResponse(colab);
    }
}
