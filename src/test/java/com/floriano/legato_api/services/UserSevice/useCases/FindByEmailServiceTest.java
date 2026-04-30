package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindByEmailServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FindByEmailService findByEmailService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = findByEmailService.execute("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void execute_NotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        User result = findByEmailService.execute("test@example.com");

        assertNull(result);
    }
}
