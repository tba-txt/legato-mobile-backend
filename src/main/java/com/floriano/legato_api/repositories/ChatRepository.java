package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.Chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
        SELECT c FROM Chat c
        JOIN c.participants p1
        JOIN c.participants p2
        WHERE p1.id = :user1Id AND p2.id = :user2Id
        """)
    Optional<Chat> findChatBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
