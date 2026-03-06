package com.floriano.legato_api.dto.NotificationDTO;

import com.floriano.legato_api.model.Notification.enums.NotificationTargetType;
import com.floriano.legato_api.model.Notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    private Long senderId;
    private Long recipientId;

    private String message;

    private NotificationType type;
    private NotificationTargetType targetType;
    private Long targetId;

    private LocalDateTime createdAt;
}

