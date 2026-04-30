package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.RecursoNaoEncontradoException;
import com.floriano.legato_api.model.Connection.Connection;
import com.floriano.legato_api.model.Connection.ConnectionStatus;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ConnectionRepository;
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
class RemoveConnectionServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RemoveConnectionService removeConnectionService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setId(1L);
        User target = new User();
        target.setId(2L);

        Connection connection = new Connection(user, target, ConnectionStatus.ACCEPTED, "");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(connectionRepository.findByUsersAndStatus(1L, 2L, ConnectionStatus.ACCEPTED)).thenReturn(Optional.of(connection));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = removeConnectionService.execute(1L, 2L);

        assertNotNull(result);
        verify(connectionRepository).delete(connection);
    }

    @Test
    void execute_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            removeConnectionService.execute(1L, 2L);
        });
    }

    @Test
    void execute_ConnectionNotFound() {
        User user = new User();
        user.setId(1L);
        User target = new User();
        target.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(connectionRepository.findByUsersAndStatus(1L, 2L, ConnectionStatus.ACCEPTED)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            removeConnectionService.execute(1L, 2L);
        });
    }
}
