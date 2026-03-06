package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser(User user);
}
