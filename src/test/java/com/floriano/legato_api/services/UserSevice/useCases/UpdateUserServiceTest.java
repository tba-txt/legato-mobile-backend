package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserUpdateDTO;
import com.floriano.legato_api.exceptions.RecursoNaoEncontradoException;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UpdateUserService updateUserService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("Old Name");

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("New Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = updateUserService.execute(1L, dto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
    }

    @Test
    void execute_UserNotFound() {
        UserUpdateDTO dto = new UserUpdateDTO();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            updateUserService.execute(1L, dto);
        });
    }
}
