package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListBlockedUsersService {

    private final UserRepository userRepository;

    public ListBlockedUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        return user.getBlockedUsers()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}