package com.floriano.legato_api.services.NotificationService.useCases;

import com.floriano.legato_api.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteNotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(Long notificationId, Long currentUserId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(currentUserId)) {
            throw new RuntimeException("You do not have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }
}
