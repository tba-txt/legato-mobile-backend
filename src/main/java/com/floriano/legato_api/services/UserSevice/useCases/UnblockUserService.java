package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UnblockUserService {

    private final UserRepository userRepository;

    public UnblockUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO execute(Long blockerId, Long targetId) {
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new UserNotFoundException("Usuário bloqueador não encontrado"));

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new UserNotFoundException("Usuário a ser desbloqueado não encontrado"));

        blocker.unblockUser(target);
        userRepository.save(blocker);

        return UserMapper.toDTO(target);
    }
}