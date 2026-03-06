package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class BlockUserService {

    private final UserRepository userRepository;

    public BlockUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO execute(Long blockerId, Long targetId) {
        if (blockerId.equals(targetId)) {
            throw new IllegalArgumentException("Um usuário não pode se bloquear.");
        }

        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new UserNotFoundException("Usuário bloqueador não encontrado"));

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new UserNotFoundException("Usuário a ser bloqueado não encontrado"));

        blocker.blockUser(target);
        userRepository.save(blocker);

        return UserMapper.toDTO(target);
    }
}
