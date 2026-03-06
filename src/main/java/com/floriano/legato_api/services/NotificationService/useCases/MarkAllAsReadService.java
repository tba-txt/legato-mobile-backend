package com.floriano.legato_api.services.NotificationService.useCases;

import com.floriano.legato_api.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkAllAsReadService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(Long recipientId) {
        notificationRepository.markAllAsReadByRecipient(recipientId);
    }
}
