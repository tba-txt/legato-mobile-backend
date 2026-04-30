package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.RecursoNaoEncontradoException;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BlockUserService blockUserService;

    @Test
    void execute_Success() {
        User blocker = new User();
        blocker.setId(1L);
        blocker.setBlockedUsers(new HashSet<>());

        User target = new User();
        target.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(blocker));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(userRepository.save(any(User.class))).thenReturn(blocker);

        User result = blockUserService.execute(1L, 2L);

        assertNotNull(result);
        assertTrue(result.getBlockedUsers().contains(target));
        verify(userRepository).save(blocker);
    }

    @Test
    void execute_BlockerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            blockUserService.execute(1L, 2L);
        });
    }

    @Test
    void execute_TargetNotFound() {
        User blocker = new User();
        blocker.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(blocker));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            blockUserService.execute(1L, 2L);
        });
    }
}
