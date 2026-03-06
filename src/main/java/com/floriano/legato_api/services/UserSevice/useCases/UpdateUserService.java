package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.dto.UserDTO.UserUpdateDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.UserSevice.utils.UserUpdateHelper;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserService {

    private UserRepository userRepository;

    public UpdateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO execute(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id " + id + " não encontrado"));

        UserUpdateHelper.updateBasicFields(user, dto);
        UserUpdateHelper.updateMusicPreferences(user, dto);
        UserUpdateHelper.updateExternalLinks(user, dto);
        UserUpdateHelper.updateLocation(user, dto);

        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        return UserMapper.toDTO(user);
    }
}
