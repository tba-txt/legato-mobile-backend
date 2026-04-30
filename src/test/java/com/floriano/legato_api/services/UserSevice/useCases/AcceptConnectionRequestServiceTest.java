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
class AcceptConnectionRequestServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AcceptConnectionRequestService acceptConnectionRequestService;

    @Test
    void execute_Success() {
        User receiver = new User();
        receiver.setId(1L);
        User sender = new User();
        sender.setId(2L);

        Connection request = new Connection();
        request.setId(1L);
        request.setReceiver(receiver);
        request.setSender(sender);
        request.setStatus(ConnectionStatus.PENDING);

        when(connectionRepository.findById(1L)).thenReturn(Optional.of(request));

        acceptConnectionRequestService.execute(1L, 1L);

        assertEquals(ConnectionStatus.ACCEPTED, request.getStatus());
        verify(connectionRepository).save(request);
    }

    @Test
    void execute_RequestNotFound() {
        when(connectionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            acceptConnectionRequestService.execute(1L, 1L);
        });
    }

    @Test
    void execute_NotReceiver() {
        User receiver = new User();
        receiver.setId(2L);
        Connection request = new Connection();
        request.setId(1L);
        request.setReceiver(receiver);

        when(connectionRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            acceptConnectionRequestService.execute(1L, 1L);
        });
    }
}
