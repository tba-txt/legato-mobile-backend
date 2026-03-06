package com.floriano.legato_api.services.CommentService.useCases;

import com.floriano.legato_api.dto.CommentDTO.CommentRequestDTO;
import com.floriano.legato_api.dto.CommentDTO.CommentResponseDTO;
import com.floriano.legato_api.exceptions.PostNotFoundException;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.CommentMapper.CommentMapper;
import com.floriano.legato_api.model.Comment.Comment;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.CommentRepository;
import com.floriano.legato_api.repositories.PostRepository;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateCommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    public CommentResponseDTO execute(Long userId, CommentRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        Comment comment = CommentMapper.toEntity(dto, user, post);

        commentRepository.save(comment);

        return CommentMapper.toResponseDTO(comment);
    }
}
