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

@Service
@AllArgsConstructor
public class DeletePostService {

    private final PostRepository postRepository;

    @Transactional
    public PostResponseDTO execute(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post não encontrado"));

        if (!post.getUser().getId().equals(userId)) {
            throw new ForbiddenActionException("Você não pode excluir este post");
        }

        postRepository.delete(post);

        return PostMapper.toResponse(post);
    }
}
