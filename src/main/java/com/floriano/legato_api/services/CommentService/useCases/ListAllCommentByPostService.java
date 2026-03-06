package com.floriano.legato_api.services.CommentService.useCases;

import com.floriano.legato_api.dto.CommentDTO.CommentResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.CommentMapper.CommentMapper;
import com.floriano.legato_api.mapper.PostMapper.PostMapper;
import com.floriano.legato_api.model.Comment.Comment;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.CommentRepository;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ListAllCommentByPostService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public List<CommentResponseDTO> execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<Comment> comments = commentRepository.findByUser(user);

        return comments.stream().map(CommentMapper::toResponseDTO).toList();
    }
}
