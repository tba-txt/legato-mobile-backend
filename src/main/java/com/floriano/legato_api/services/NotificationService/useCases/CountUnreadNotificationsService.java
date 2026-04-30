package com.floriano.legato_api.services.NotificationService.useCases;

import com.floriano.legato_api.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountUnreadNotificationsService {

    private final NotificationRepository notificationRepository;

    public long execute(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }
}