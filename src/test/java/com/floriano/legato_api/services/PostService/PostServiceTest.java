package com.floriano.legato_api.services.PostService;

import com.floriano.legato_api.dto.PostDTO.PostRequestDTO;
import com.floriano.legato_api.exceptions.PostNotFoundException;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private PostService postService;

    @Test
    void createPost_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        PostRequestDTO requestDTO = new PostRequestDTO();
        requestDTO.setContent("Test content");

        when(userService.findById(userId)).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.createPost(requestDTO, userId);

        assertNotNull(result);
        assertEquals("Test content", result.getContent());
        assertEquals(user, result.getUser());
    }

    @Test
    void deletePost_Success() {
        Long postId = 1L;
        Long userId = 1L;
        Post post = new Post();
        User user = new User();
        user.setId(userId);
        post.setId(postId);
        post.setUser(user);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> postService.deletePost(postId, userId));
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_PostNotFound_ThrowsException() {
        Long postId = 1L;
        Long userId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> {
            postService.deletePost(postId, userId);
        });
    }

    @Test
    void deletePost_NotOwner_ThrowsException() {
        Long postId = 1L;
        Long ownerId = 1L;
        Long requesterId = 2L;
        Post post = new Post();
        User owner = new User();
        owner.setId(ownerId);
        post.setId(postId);
        post.setUser(owner);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(PostNotFoundException.class, () -> {
            postService.deletePost(postId, requesterId);
        });
    }
}
