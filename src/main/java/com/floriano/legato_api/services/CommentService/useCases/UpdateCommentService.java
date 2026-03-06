package com.floriano.legato_api.services.CommentService.useCases;


import com.floriano.legato_api.dto.CommentDTO.CommentResponseDTO;
import com.floriano.legato_api.exceptions.ForbiddenActionException;
import com.floriano.legato_api.exceptions.PostNotFoundException;
import com.floriano.legato_api.mapper.CommentMapper.CommentMapper;
import com.floriano.legato_api.model.Comment.Comment;
import com.floriano.legato_api.repositories.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UpdateCommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDTO execute(Long userId, Long postId, String content) {

        Comment comment = commentRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Comentário não encontrado"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenActionException("Você não pode editar este comentário");
        }

        comment.setContent(content);

        return CommentMapper.toResponseDTO(comment);
    }
}
