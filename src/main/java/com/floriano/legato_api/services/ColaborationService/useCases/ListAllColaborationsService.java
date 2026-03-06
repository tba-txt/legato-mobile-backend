package com.floriano.legato_api.services.ColaborationService.useCases;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationResponseDTO;
import com.floriano.legato_api.mapper.ColaborationMapper.ColaborationMapper;
import com.floriano.legato_api.model.Colaboration.Colaboration;
import com.floriano.legato_api.repositories.ColaborationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAllColaborationsService {

    private final ColaborationRepository colaborationRepository;

    public List<ColaborationResponseDTO> execute() {
        List<Colaboration> colaborations = colaborationRepository.findAll();

        List<ColaborationResponseDTO> colaborationResponseDTOS = colaborations.stream().map(ColaborationMapper::toResponse).toList();

        return colaborationResponseDTOS;
    }
}
