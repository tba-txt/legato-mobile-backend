package com.floriano.legato_api.services.ChatService;

import com.floriano.legato_api.model.Chat.Chat;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void getOrCreateChatBetween_ChatExists() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        Chat existingChat = new Chat();

        when(chatRepository.findChatByParticipants(user1, user2)).thenReturn(Optional.of(existingChat));

        Chat result = chatService.getOrCreateChatBetween(user1, user2);

        assertEquals(existingChat, result);
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void getOrCreateChatBetween_ChatDoesNotExist() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(chatRepository.findChatByParticipants(user1, user2)).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Chat result = chatService.getOrCreateChatBetween(user1, user2);

        assertNotNull(result);
        assertTrue(result.getParticipants().contains(user1));
        assertTrue(result.getParticipants().contains(user2));
        verify(chatRepository).save(any(Chat.class));
    }
}
