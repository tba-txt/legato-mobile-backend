package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.user.UserRequestDTO;
import com.floriano.legato_api.exceptions.users.UserNotFoundException;
import com.floriano.legato_api.model.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestUpdateUser {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Test update user when successful")
    void testUpdateUser_Successful() {
        Long userId = 1L;
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Updated Name",
                "updated@example.com",
                "password"
        );
        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        User result = userService.update(userId, userRequestDTO);

        assertEquals(userRequestDTO.name(), result.getName());
        assertEquals(userRequestDTO.email(), result.getEmail());
    }

    @Test
    @DisplayName("Test update user when user not found")
    void testUpdateUser_UserNotFound() {
        Long userId = 1L;
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Updated Name",
                "updated@example.com",
                "password"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(userId, userRequestDTO));
    }
}
