package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UnfollowUserService {
    private final UserRepository userRepository;

    public UnfollowUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDTO execute(Long followerId, Long targetId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Usuário seguidor com id " + followerId + " não encontrado"));

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new UserNotFoundException("Usuário alvo com id " + targetId + " não encontrado"));

        follower.unfollow(target);
        userRepository.save(follower);
        userRepository.save(target);

        return UserMapper.toDTO(target);
    }
}
