package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoveConnectionService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO execute(Long userId, Long targetId) {
        if (userId.equals(targetId)) {
            throw new IllegalArgumentException("Um usuário não pode remover conexão consigo mesmo.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id " + userId + " não encontrado."));

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id " + targetId + " não encontrado."));
        
        user.removeConnection(target);

        userRepository.save(user);
        userRepository.save(target);

        return UserMapper.toDTO(user);
    }
}
