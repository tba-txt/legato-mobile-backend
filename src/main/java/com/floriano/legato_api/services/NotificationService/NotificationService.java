package com.floriano.legato_api.services.NotificationService;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.dto.NotificationDTO.NotificationResponseDTO;
import com.floriano.legato_api.services.NotificationService.useCases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final CreateNotificationService createNotificationService;
    private final ListNotificationsByRecipientService listNotificationsByRecipientService;
    private final DeleteNotificationService deleteNotificationService;
    private final MarkAllAsReadService markAllAsReadService;

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> findAllByRecipient(Long recipientId) {
        return listNotificationsByRecipientService.execute(recipientId);
    }

    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO dto) {
        return createNotificationService.execute(dto);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long currentUserId) {
        deleteNotificationService.execute(notificationId, currentUserId);
    }

    @Transactional
    public void markAllAsRead(Long recipientId) {
        markAllAsReadService.execute(recipientId);
    }
}
