package com.floriano.legato_api.mapper.NotificationMapper;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.dto.NotificationDTO.NotificationResponseDTO;
import com.floriano.legato_api.model.Notification.Notification;
import com.floriano.legato_api.model.User.User;

import java.time.LocalDateTime;

public class NotificationMapper {

    public static Notification toEntity(NotificationRequestDTO dto, User sender, User recipient) {
        Notification n = new Notification();

        n.setSender(sender);
        n.setRecipient(recipient);

        n.setMessage(dto.getMessage());
        n.setType(dto.getType());
        n.setTargetType(dto.getTargetType());
        n.setTargetId(dto.getTargetId());

        n.setRead(false);
        n.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());

        return n;
    }

    public static NotificationResponseDTO toResponseDTO(Notification notification) {

        NotificationResponseDTO dto = new NotificationResponseDTO();

        dto.setId(notification.getId());
        dto.setSenderName(notification.getSender() != null ? notification.getSender().getUsername() : null);
        dto.setRecipientName(notification.getRecipient() != null ? notification.getRecipient().getUsername() : null);

        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setTimeAgo(notification.getTimeAgo());

        dto.setType(notification.getType());
        dto.setTargetType(notification.getTargetType());
        dto.setTargetId(notification.getTargetId());

        return dto;
    }
}

