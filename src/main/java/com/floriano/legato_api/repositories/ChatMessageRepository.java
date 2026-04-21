package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.ChatMessage.ChatMessage;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long > {

    // Adicione isso no seu ChatMessageRepository.java
    @Modifying
    @Query("UPDATE ChatMessage m SET m.status = 'READ', m.readAt = :readAt WHERE m.chat.id = :chatId AND m.receiver.id = :receiverId AND m.status != 'READ'")
    void markMessagesAsRead(@Param("chatId") Long chatId, @Param("receiverId") Long receiverId, @Param("readAt") LocalDateTime readAt);
}
