package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListConnectionsService {

    private final UserRepository userRepository;

    public List<UserResponseDTO> execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return user.getConnections()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}
