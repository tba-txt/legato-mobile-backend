package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.RecursoNaoEncontradoException;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListBlockedUsersServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ListBlockedUsersService listBlockedUsersService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setId(1L);
        User blockedUser = new User();
        blockedUser.setId(2L);
        user.setBlockedUsers(new HashSet<>(Collections.singletonList(blockedUser)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<UserResponseDTO> result = listBlockedUsersService.execute(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void execute_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            listBlockedUsersService.execute(1L);
        });
    }
}
