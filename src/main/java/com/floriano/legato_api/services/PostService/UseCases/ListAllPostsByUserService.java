package com.floriano.legato_api.services.PostService.UseCases;

import com.floriano.legato_api.dto.PostDTO.PostResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.PostMapper.PostMapper;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.PostRepository;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ListAllPostsByUserService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostResponseDTO> execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<Post> posts = postRepository.findByUser(user);

        return posts.stream().map(PostMapper::toResponse).toList();
    }
}
