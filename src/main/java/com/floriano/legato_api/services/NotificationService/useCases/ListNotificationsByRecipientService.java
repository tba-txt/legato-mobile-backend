package com.floriano.legato_api.services.NotificationService.useCases;

import com.floriano.legato_api.dto.NotificationDTO.NotificationResponseDTO;
import com.floriano.legato_api.mapper.NotificationMapper.NotificationMapper;
import com.floriano.legato_api.model.Notification.Notification;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.NotificationRepository;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListNotificationsByRecipientService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<NotificationResponseDTO> execute(Long recipientUser) {
        User user = userRepository.findById(recipientUser)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Notification> notifications = notificationRepository.findAllByRecipientOrderByCreatedAtDesc(user);

        List<NotificationResponseDTO> notificationResponseDTOS = notifications.stream().map(NotificationMapper::toResponseDTO).toList();

        return notificationResponseDTOS;
    }
}
