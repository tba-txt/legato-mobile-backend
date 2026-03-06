package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.Notification.Notification;
import com.floriano.legato_api.model.User.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByRecipientOrderByCreatedAtDesc(User recipient);

    @Transactional
    @Modifying
    @Query("UPDATE notifications n SET n.read = true WHERE n.recipient.id = :recipientId")
    void markAllAsReadByRecipient(Long recipientId);
}
