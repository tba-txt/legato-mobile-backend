package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserListDTO;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAllUsersServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ListAllUsersService listAllUsersService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserListDTO> result = listAllUsersService.execute();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
    }

    @Test
    void execute_NoUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserListDTO> result = listAllUsersService.execute();

        assertTrue(result.isEmpty());
    }
}
