package com.floriano.legato_api.services.NotificationService.useCases;

import com.floriano.legato_api.exceptions.ResourceNotFoundException;
import com.floriano.legato_api.model.Notification.Notification;
import com.floriano.legato_api.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadNotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new IllegalStateException("Ação não permitida para este usuário.");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}