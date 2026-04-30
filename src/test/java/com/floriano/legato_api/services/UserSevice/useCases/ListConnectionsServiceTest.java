package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListConnectionsServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ConnectionRepository connectionRepository;
    @InjectMocks
    private ListConnectionsService listConnectionsService;

    @Test
    void execute_Success() {
        User user = new User();
        user.setId(1L);
        User connectedUser = new User();
        connectedUser.setId(2L);

        Connection connection = new Connection(user, connectedUser, ConnectionStatus.ACCEPTED, "");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(connectionRepository.findBySenderIdOrReceiverIdAndStatus(1L, 1L, ConnectionStatus.ACCEPTED))
                .thenReturn(Collections.singletonList(connection));

        List<UserResponseDTO> result = listConnectionsService.execute(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void execute_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            listConnectionsService.execute(1L);
        });
    }
}
