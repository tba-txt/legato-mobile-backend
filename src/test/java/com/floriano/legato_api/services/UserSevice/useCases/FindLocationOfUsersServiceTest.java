package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.LocationUserDTO;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindLocationOfUsersServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FindLocationOfUsersService findLocationOfUsersService;

    @Test
    void execute_Success() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLocation(new Point(10, 10));

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setLocation(new Point(10.01, 10.01));

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(otherUser));

        List<LocationUserDTO> result = findLocationOfUsersService.execute(1L, 10.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void execute_CurrentUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        List<LocationUserDTO> result = findLocationOfUsersService.execute(1L, 10.0);

        assertTrue(result.isEmpty());
    }
}
