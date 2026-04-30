package com.floriano.legato_api.services.ChatService;

import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import com.floriano.legato_api.repositories.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    void saveMessage_Success() {
        ChatMessage message = new ChatMessage();
        message.setContent("Hello");

        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(message);

        ChatMessage result = chatMessageService.saveMessage(message);

        assertNotNull(result);
        assertEquals("Hello", result.getContent());
    }
}
