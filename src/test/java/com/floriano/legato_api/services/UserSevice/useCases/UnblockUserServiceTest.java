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
class UnblockUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UnblockUserService unblockUserService;

    @Test
    void execute_Success() {
        User blocker = new User();
        blocker.setId(1L);
        User target = new User();
        target.setId(2L);
        blocker.setBlockedUsers(new HashSet<>(java.util.Collections.singletonList(target)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(blocker));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(userRepository.save(any(User.class))).thenReturn(blocker);

        User result = unblockUserService.execute(1L, 2L);

        assertNotNull(result);
        assertFalse(result.getBlockedUsers().contains(target));
        verify(userRepository).save(blocker);
    }

    @Test
    void execute_BlockerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            unblockUserService.execute(1L, 2L);
        });
    }
}
