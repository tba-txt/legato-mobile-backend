package com.floriano.legato_api.services.PostService.UseCases;

import com.floriano.legato_api.dto.PostDTO.PostResponseDTO;
import com.floriano.legato_api.exceptions.ForbiddenActionException;
import com.floriano.legato_api.exceptions.PostNotFoundException;
import com.floriano.legato_api.mapper.PostMapper.PostMapper;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.repositories.PostRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UpdatePostService {

    private final PostRepository postRepository;

    @Transactional
    public PostResponseDTO execute(Long userId, Long postId, String content) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post não encontrado"));

        if (!post.getUser().getId().equals(userId)) {
            throw new ForbiddenActionException("Você não pode editar este post");
        }

        LocalDateTime createdAt = post.getCreatedAt();
        LocalDateTime agora = LocalDateTime.now();

        if (createdAt != null && Duration.between(createdAt, agora).toMinutes() > 10) {
            throw new ForbiddenActionException("O tempo limite de 10 minutos para edição já expirou.");
        }

        post.setContent(content);

        return PostMapper.toResponse(post);
    }
}