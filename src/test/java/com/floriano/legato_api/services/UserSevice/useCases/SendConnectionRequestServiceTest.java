package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.OperacaoNaoPermitidaException;
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
class SendConnectionRequestServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ConnectionRepository connectionRepository;
    @InjectMocks
    private SendConnectionRequestService sendConnectionRequestService;

    @Test
    void execute_Success() {
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(connectionRepository.findByUsersAndStatus(1L, 2L, ConnectionStatus.PENDING)).thenReturn(Optional.empty());
        when(connectionRepository.save(any(Connection.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = sendConnectionRequestService.execute(1L, 2L, "Hello");

        assertNotNull(result);
        verify(connectionRepository).save(any(Connection.class));
    }

    @Test
    void execute_SenderNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            sendConnectionRequestService.execute(1L, 2L, "Hello");
        });
    }

    @Test
    void execute_RequestAlreadyExists() {
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(connectionRepository.findByUsersAndStatus(1L, 2L, ConnectionStatus.PENDING)).thenReturn(Optional.of(new Connection()));

        assertThrows(OperacaoNaoPermitidaException.class, () -> {
            sendConnectionRequestService.execute(1L, 2L, "Hello");
        });
    }
}
