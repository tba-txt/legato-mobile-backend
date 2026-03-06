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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateUserCardsImageService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private static final int MAX_PHOTOS = 4;

    @Transactional
    public UserResponseDTO execute(Long userId, MultipartFile file, int index) {

        if (index < 0 || index >= MAX_PHOTOS) {
            throw new IllegalArgumentException("Índice deve estar entre 0 e " + (MAX_PHOTOS - 1));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<String> photos = user.getPhotosCard();
        if (photos == null) {
            photos = new ArrayList<>();
            user.setPhotosCard(photos);
        }

        while (photos.size() <= index) {
            photos.add(null);
        }

        String oldUrl = photos.get(index);
        if (oldUrl != null) {
            cloudinaryService.deleteFile(oldUrl);
        }

        String newUrl = cloudinaryService.uploadFile(file, "users/" + userId);
        photos.set(index, newUrl);

        userRepository.save(user);
        return UserMapper.toDTO(user);
    }
}