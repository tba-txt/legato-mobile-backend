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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @InjectMocks
    private DeleteUserService deleteUserService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setId(1L);
        user.setProfilePicture("profile_pic_url");
        user.setBanner("banner_url");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        doNothing().when(cloudinaryService).deleteFile(anyString());

        deleteUserService.execute(1L);

        verify(cloudinaryService).deleteFile("profile_pic_url");
        verify(cloudinaryService).deleteFile("banner_url");
        verify(userRepository).delete(user);
    }

    @Test
    void execute_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            deleteUserService.execute(1L);
        });
    }
}
