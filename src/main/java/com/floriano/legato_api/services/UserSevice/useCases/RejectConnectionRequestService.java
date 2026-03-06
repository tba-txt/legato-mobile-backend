package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.model.Connection.ConnectionRequest;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ConnectionRequestRepository;
import com.floriano.legato_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RejectConnectionRequestService {

    private final UserRepository userRepository;
    private final ConnectionRequestRepository connectionRequestRepository;

    @Transactional
    public void execute(Long receiverId, Long requestId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));


        if(!Objects.equals(receiver.getId(), request.getReceiver().getId())) {
            throw new IllegalArgumentException("Usuário não tem permissão para rejeitar este pedido de conexão");
        }

        receiver.rejectConnectionRequest(request);
        connectionRequestRepository.save(request);
    }
}