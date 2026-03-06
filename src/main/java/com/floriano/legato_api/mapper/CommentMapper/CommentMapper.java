package com.floriano.legato_api.mapper.CommentMapper;

import com.floriano.legato_api.dto.CommentDTO.CommentRequestDTO;
import com.floriano.legato_api.dto.CommentDTO.CommentResponseDTO;
import com.floriano.legato_api.model.Comment.Comment;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;

public class CommentMapper {

    public static Comment toEntity(CommentRequestDTO dto, User user, Post post) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(dto.getContent());

        return comment;
    }

    public static CommentResponseDTO toResponseDTO(Comment comment) {
        CommentResponseDTO response = new CommentResponseDTO();
        response.setUserId(comment.getUser().getId());
        response.setContent(comment.getContent());
        response.setTimeAgo(comment.getTimeAgo());
        return response;
    }
}
