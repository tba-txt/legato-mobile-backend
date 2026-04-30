package com.floriano.legato_api.services.ChatService;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import com.floriano.legato_api.model.Notification.enums.NotificationTargetType;
import com.floriano.legato_api.model.Notification.enums.NotificationType;
import com.floriano.legato_api.repositories.ChatMessageRepository;
import com.floriano.legato_api.services.NotificationService.useCases.CreateNotificationService;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final CreateNotificationService createNotificationService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, CreateNotificationService createNotificationService) {
        this.chatMessageRepository = chatMessageRepository;
        this.createNotificationService = createNotificationService;
    }

    @Transactional
    public void markMessagesAsRead(Long chatId, Long receiverId) {
        chatMessageRepository.markMessagesAsRead(chatId, receiverId, LocalDateTime.now());
    }

    public ChatMessage saveMessage(ChatMessage message) {
        ChatMessage savedMessage = chatMessageRepository.save(message);

        if (savedMessage.getSender() != null && savedMessage.getReceiver() != null) {
            NotificationRequestDTO dto = new NotificationRequestDTO();
            dto.setSenderId(savedMessage.getSender().getId());
            dto.setRecipientId(savedMessage.getReceiver().getId());
            dto.setMessage("@" + savedMessage.getSender().getUsername() + " enviou uma mensagem.");
            dto.setType(NotificationType.MESSAGE_RECEIVED);
            dto.setTargetType(NotificationTargetType.MESSAGE);
            
            Long targetId = (savedMessage.getChat() != null) ? savedMessage.getChat().getId() : savedMessage.getSender().getId();
            dto.setTargetId(targetId); 
            
            dto.setCreatedAt(null);

            createNotificationService.execute(dto);
        }

        return savedMessage;
    }

    public ChatMessage findById(Long id) {
        return chatMessageRepository.findById(id).orElse(null);
    }
}