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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveUserCardImageServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @InjectMocks
    private RemoveUserCardImageService removeUserCardImageService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setId(1L);
        List<Image> cardImages = new ArrayList<>();
        Image imageToRemove = new Image();
        imageToRemove.setId(1L);
        imageToRemove.setUrl("image_url");
        cardImages.add(imageToRemove);
        user.setCardImgs(cardImages);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = removeUserCardImageService.execute(1L, 0);

        assertNotNull(result);
        assertTrue(result.getCardImgs().isEmpty());
        verify(cloudinaryService).deleteFile("image_url");
        verify(imageRepository).delete(imageToRemove);
    }

    @Test
    void execute_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            removeUserCardImageService.execute(1L, 0);
        });
    }

    @Test
    void execute_IndexOutOfBounds() {
        User user = new User();
        user.setId(1L);
        user.setCardImgs(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IndexOutOfBoundsException.class, () -> {
            removeUserCardImageService.execute(1L, 0);
        });
    }
}
