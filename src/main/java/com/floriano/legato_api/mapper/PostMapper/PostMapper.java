package com.floriano.legato_api.mapper.PostMapper;

import com.floriano.legato_api.dto.PostDTO.PostResponseDTO;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;

public class PostMapper {

    public static PostResponseDTO toResponse(Post post) {

        User user = post.getUser();

        return new PostResponseDTO(
                post.getId(),
                post.getContent(),

                post.getTypeMedia() != null ? post.getTypeMedia() : null,
                post.getUrlMedia() != null ? post.getUrlMedia() : null,

                post.getLikes().size(),
                post.getComments().size(),
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),

                post.getCreatedAt()
        );
    }
}
