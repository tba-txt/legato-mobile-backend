package com.floriano.legato_api.services.ChatService;

import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import com.floriano.legato_api.repositories.ChatMessageRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }
}
