package com.floriano.legato_api.services.ColaborationService.useCases;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.ColaborationMapper.ColaborationMapper;
import com.floriano.legato_api.model.Colaboration.Colaboration;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ColaborationRepository;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListByUserColaborationService {

    private final ColaborationRepository colaborationRepository;
    private final UserRepository userRepository;


    public List<ColaborationResponseDTO> execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado!"));

        List<Colaboration> colabs = colaborationRepository.findByUser(user);

        List<ColaborationResponseDTO> colaborationResponseDTOS = colabs.stream().map(ColaborationMapper::toResponse).toList();

        return colaborationResponseDTOS;
    }
}
