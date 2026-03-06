package com.floriano.legato_api.services.NotificationService.useCases;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.dto.NotificationDTO.NotificationResponseDTO;
import com.floriano.legato_api.mapper.NotificationMapper.NotificationMapper;
import com.floriano.legato_api.model.Notification.Notification;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.NotificationRepository;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationResponseDTO execute(NotificationRequestDTO dto) {
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        User recipient = userRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        dto.setCreatedAt(LocalDateTime.now());
        dto.setSenderId(sender.getId());

        Notification notification = NotificationMapper.toEntity(dto, sender, recipient);

        notification = notificationRepository.save(notification);

        return NotificationMapper.toResponseDTO(notification);
    }
}
