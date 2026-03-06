package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.model.Connection.ConnectionRequest;
import com.floriano.legato_api.model.Notification.enums.NotificationTargetType;
import com.floriano.legato_api.model.Notification.enums.NotificationType;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ConnectionRequestRepository;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.NotificationService.useCases.CreateNotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AcceptConnectionRequestService {

    private final UserRepository userRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final CreateNotificationService createNotificationService;

    @Transactional
    public void execute(Long receiverId, Long requestId) {

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (!Objects.equals(receiver.getId(), request.getReceiver().getId())) {
            throw new IllegalArgumentException("Usuário não tem permissão para aceitar este pedido de conexão");
        }

        User sender = request.getSender();

        receiver.acceptConnectionRequest(request);
        connectionRequestRepository.save(request);

        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setSenderId(receiver.getId());
        dto.setRecipientId(sender.getId());
        dto.setMessage(receiver.getUsername() + " aceitou seu pedido de conexão.");
        dto.setType(NotificationType.CONNECTION_ACCEPTED);
        dto.setTargetType(NotificationTargetType.USER);
        dto.setTargetId(receiver.getId());
        dto.setCreatedAt(null);

        createNotificationService.execute(dto);
    }
}

