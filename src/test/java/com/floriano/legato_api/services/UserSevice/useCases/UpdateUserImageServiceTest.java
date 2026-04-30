package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.RecursoNaoEncontradoException;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.CloudinaryService.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserImageServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @InjectMocks
    private UpdateUserImageService updateUserImageService;

    @Test
    void execute_UpdateProfilePicture() {
        User user = new User();
        user.setId(1L);
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cloudinaryService.uploadFile(any(MultipartFile.class), anyString())).thenReturn("new_profile_pic_url");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = updateUserImageService.execute(1L, file, "profile");

        assertNotNull(result);
        assertEquals("new_profile_pic_url", result.getProfilePicture());
    }

    @Test
    void execute_UpdateBanner() {
        User user = new User();
        user.setId(1L);
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cloudinaryService.uploadFile(any(MultipartFile.class), anyString())).thenReturn("new_banner_url");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = updateUserImageService.execute(1L, file, "banner");

        assertNotNull(result);
        assertEquals("new_banner_url", result.getBanner());
    }

    @Test
    void execute_UserNotFound() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            updateUserImageService.execute(1L, file, "profile");
        });
    }
}
