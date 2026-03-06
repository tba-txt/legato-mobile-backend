package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.CloudinaryService.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoveUserCardImageService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    private static final int MAX_PHOTOS = 4;

    @Transactional
    public UserResponseDTO execute(Long userId, int index) {

        if (index < 0 || index >= MAX_PHOTOS) {
            throw new IllegalArgumentException("Índice deve estar entre 0 e " + (MAX_PHOTOS - 1));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<String> photos = user.getPhotosCard();

        if (photos == null || photos.size() <= index || photos.get(index) == null) {
            return UserMapper.toDTO(user); // nada para remover
        }

        String imageUrl = photos.get(index);

        cloudinaryService.deleteFile(imageUrl);
        photos.set(index, null);

        userRepository.save(user);
        return UserMapper.toDTO(user);
    }
}
