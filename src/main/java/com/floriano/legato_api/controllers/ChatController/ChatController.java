package com.floriano.legato_api.controllers.ChatController;

import com.floriano.legato_api.dto.ChatDTO.ChatMessageDTO;
import com.floriano.legato_api.model.Chat.Chat;
import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.services.ChatService.ChatMessageService;
import com.floriano.legato_api.services.ChatService.ChatService;
import com.floriano.legato_api.services.UserSevice.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;
    private final UserService userService;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatService chatService,
                          ChatMessageService chatMessageService,
                          UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
    }

    @Transactional
    @MessageMapping("/sendMessage")
    public void sendPrivateMessage(ChatMessage message,
                                   SimpMessageHeaderAccessor headerAccessor,
                                   Principal principal) {
        try {
            String fromEmail = principal.getName();
            User sender = userService.findByEmail(fromEmail);
            User receiver = userService.findById(message.getReceiver().getId());

            if (sender == null || receiver == null) {
                logger.error("Remetente ({}) ou destinatário inválido.", fromEmail);
                return;
            }

            Chat chat = chatService.getOrCreateChatBetween(sender, receiver);

            message.setChat(chat);
            message.setSender(sender);
            message.setTimestamp(LocalDateTime.now());

            ChatMessage saved = chatMessageService.saveMessage(message);
            chat.addMessage(saved);
            chatService.saveChat(chat);

            logger.info("Mensagem salva no chat {}: {}", chat.getId(), saved.getContent());

            messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),
                    "/queue/messages",
                    ChatMessageDTO.from(saved)
            );

        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem: {}", e.getMessage(), e);
        }
    }
}
