package com.floriano.legato_api.services.CommentService;

import com.floriano.legato_api.dto.CommentDTO.CommentRequestDTO;
import com.floriano.legato_api.exceptions.PostNotFoundException;
import com.floriano.legato_api.model.Comment.Comment;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.CommentRepository;
import com.floriano.legato_api.repositories.PostRepository;
import com.floriano.legato_api.services.UserSevice.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private CommentService commentService;

    @Test
    void addCommentToPost_Success() {
        Long postId = 1L;
        Long userId = 1L;
        Post post = new Post();
        post.setId(postId);
        User user = new User();
        user.setId(userId);
        CommentRequestDTO requestDTO = new CommentRequestDTO();
        requestDTO.setContent("Test comment");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findById(userId)).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.addCommentToPost(postId, requestDTO, userId);

        assertNotNull(result);
        assertEquals("Test comment", result.getContent());
        assertEquals(post, result.getPost());
        assertEquals(user, result.getUser());
    }

    @Test
    void addCommentToPost_PostNotFound_ThrowsException() {
        Long postId = 1L;
        Long userId = 1L;
        CommentRequestDTO requestDTO = new CommentRequestDTO();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> {
            commentService.addCommentToPost(postId, requestDTO, userId);
        });
    }
}
