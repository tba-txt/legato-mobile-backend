package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.Comment.Comment;
import com.floriano.legato_api.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUser(User user);
}
