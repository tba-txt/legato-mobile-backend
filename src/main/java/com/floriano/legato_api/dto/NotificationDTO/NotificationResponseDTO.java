package com.floriano.legato_api.dto.NotificationDTO;

import com.floriano.legato_api.model.Notification.enums.NotificationTargetType;
import com.floriano.legato_api.model.Notification.enums.NotificationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;

    private String senderName;
    private String recipientName;

    private String message;
    private boolean read;
    private String timeAgo;

    private NotificationType type;
    private NotificationTargetType targetType;
    private Long targetId;
}

