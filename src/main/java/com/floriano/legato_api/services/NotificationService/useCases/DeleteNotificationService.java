package com.floriano.legato_api.services.NotificationService.useCases;

import com.floriano.legato_api.exceptions.ForbiddenActionException;
import com.floriano.legato_api.exceptions.ResourceNotFoundException;
import com.floriano.legato_api.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.floriano.legato_api.model.Notification.Notification;

@Service
@RequiredArgsConstructor
public class DeleteNotificationService {

    private final NotificationRepository notificationRepository;


    @Transactional
    public void execute(Long notificationId, Long currentUserId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada com ID: " + notificationId));

    if (!notification.getRecipient().getId().equals(currentUserId)) {
            throw new ForbiddenActionException("Você não tem permissão para deletar esta notificação."); 
        }
        notificationRepository.delete(notification);
    }
}
