package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.user.UserRequestDTO;
import com.floriano.legato_api.model.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestCreateUser {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Test create user when successful")
    void testCreateUser_Successful() {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Gabriel",
                "gabriel@example.com",
                "123456"
        );
        User user = new User(userRequestDTO);
        user.setId(1L);

        when(passwordEncoder.encode(userRequestDTO.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(userRequestDTO);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }
}
