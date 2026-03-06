package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
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
public class SendConnectionRequestService {

    private final UserRepository userRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final CreateNotificationService createNotificationService;

    @Transactional
    public UserResponseDTO execute(Long senderId, Long receiverId, String message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Usuário remetente não encontrado"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Usuário destinatário não encontrado"));

        boolean alreadyPending = connectionRequestRepository.existsBySenderAndReceiverAndStatusPending(sender, receiver);
        if (alreadyPending)
            throw new IllegalArgumentException("Já existe um pedido pendente entre esses usuários.");

        if (Objects.equals(sender.getId(), receiverId))
            throw new IllegalArgumentException("Usuário não pode enviar conexão para si mesmo.");

        ConnectionRequest request = sender.sendConnectionRequest(receiver, message);
        connectionRequestRepository.save(request);

        NotificationRequestDTO notificationDTO = new NotificationRequestDTO();
        notificationDTO.setSenderId(sender.getId());
        notificationDTO.setRecipientId(receiver.getId());
        notificationDTO.setMessage("Você recebeu um pedido de conexão de " + sender.getUsername());
        notificationDTO.setType(NotificationType.CONNECTION_REQUEST);
        notificationDTO.setTargetType(NotificationTargetType.USER);
        notificationDTO.setTargetId(sender.getId());
        notificationDTO.setCreatedAt(null);

        createNotificationService.execute(notificationDTO);

        return UserMapper.toDTO(receiver);
    }
}