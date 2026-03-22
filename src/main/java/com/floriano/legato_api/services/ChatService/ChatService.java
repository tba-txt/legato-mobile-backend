package com.floriano.legato_api.services.ChatService;

import com.floriano.legato_api.model.Chat.Chat;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public List<Chat> getChatsByUser(User user) {
        return chatRepository.findAllByParticipantsContaining(user);
    }

    public Chat getChatByUser(Long getUser1, Long getUser2) {
        return chatRepository.findChatBetweenUsers(getUser1, getUser2)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));
    }

    public Chat getChatById(Long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));
    }

    public Optional<Chat> findChatBetween(User user1, User user2) {
        return chatRepository.findChatBetweenUsers(user1.getId(), user2.getId());
    }

    public Chat getOrCreateChatBetween(User user1, User user2) {
        return findChatBetween(user1, user2).orElseGet(() -> {
            Chat newChat = new Chat();
            newChat.getParticipants().add(user1);
            newChat.getParticipants().add(user2);
            return chatRepository.save(newChat);
        });
    }
}