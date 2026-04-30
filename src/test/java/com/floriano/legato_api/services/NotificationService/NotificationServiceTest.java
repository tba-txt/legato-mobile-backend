package com.floriano.legato_api.services.NotificationService;

import com.floriano.legato_api.model.Notification.Notification;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void getNotificationsByUser_Success() {
        User user = new User();
        user.setId(1L);
        Notification notification = new Notification();
        notification.setUser(user);

        when(notificationRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(Collections.singletonList(notification));

        List<Notification> result = notificationService.getNotificationsByUser(user);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void markNotificationsAsRead_Success() {
        User user = new User();
        user.setId(1L);
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setRead(false);
        List<Notification> notifications = Collections.singletonList(notification);

        when(notificationRepository.findByUserAndReadIsFalse(user)).thenReturn(notifications);

        notificationService.markNotificationsAsRead(user);

        assertTrue(notification.isRead());
        verify(notificationRepository).saveAll(notifications);
    }
}
