package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long > {
}
