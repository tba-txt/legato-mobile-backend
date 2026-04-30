package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.RecursoNaoEncontradoException;
import com.floriano.legato_api.model.Image.Image;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ImageRepository;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.CloudinaryService.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserCardsImageServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @InjectMocks
    private UpdateUserCardsImageService updateUserCardsImageService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setId(1L);
        user.setCardImgs(new ArrayList<>());
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cloudinaryService.uploadFile(any(MultipartFile.class), anyString())).thenReturn("new_image_url");
        when(imageRepository.save(any(Image.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = updateUserCardsImageService.execute(1L, file, 0);

        assertNotNull(result);
        assertEquals(1, result.getCardImgs().size());
        assertEquals("new_image_url", result.getCardImgs().get(0).getUrl());
    }

    @Test
    void execute_UserNotFound() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            updateUserCardsImageService.execute(1L, file, 0);
        });
    }
}
