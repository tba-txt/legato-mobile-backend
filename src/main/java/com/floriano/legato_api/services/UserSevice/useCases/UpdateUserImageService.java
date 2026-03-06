package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.CloudinaryService.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateUserImageService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public UserResponseDTO execute(Long userId, MultipartFile file, String imageType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        String url = cloudinaryService.uploadFile(file, "users/" + userId);

        switch (imageType) {
            case "profile" -> user.setProfilePicture(url);
            case "banner" -> user.setProfileBanner(url);
            default -> throw new IllegalArgumentException("Tipo de imagem inválido");
        }

        userRepository.save(user);

        return UserMapper.toDTO(user);
    }
}
