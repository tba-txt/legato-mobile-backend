package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.PostService.UseCases.ListAllPostsByUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindUserByUsername {

    private final UserRepository userRepository;
    private final ListAllPostsByUserService listAllPostsByUserService;

    public UserResponseDTO execute(String username, boolean includePosts) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        UserResponseDTO dto = UserMapper.toDTO(user);

        if (includePosts) {
            var posts = listAllPostsByUserService.execute(user.getId());

            dto.setPostsIds(
                    posts.stream()
                            .map(p -> p.getId())
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }
}
