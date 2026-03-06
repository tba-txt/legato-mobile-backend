package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.Connection.ConnectionRequest;
import com.floriano.legato_api.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END " +
            "FROM ConnectionRequest cr " +
            "WHERE cr.sender = :sender AND cr.receiver = :receiver AND cr.status = 'PENDING'")
    boolean existsBySenderAndReceiverAndStatusPending(User sender, User receiver);
}

