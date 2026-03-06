package com.floriano.legato_api.services.PostService;

import com.floriano.legato_api.dto.PostDTO.PostResponseDTO;
import com.floriano.legato_api.services.PostService.UseCases.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final CreatePostService createPostService;
    private final ListAllPostsByUserService listAllPostsByUserService;
    private final UpdatePostService updatePostService;
    private final DeletePostService deletePostService;
    private final ToggleLikeService toggleLikeService;

    public PostResponseDTO createPost(Long userId, String dto, MultipartFile mediaFile) {
        return  createPostService.execute(userId, dto, mediaFile);
    }

    public List<PostResponseDTO> listPosts(long userId) {
        return listAllPostsByUserService.execute(userId);
    }

    public PostResponseDTO updatePostService(Long userId, Long postId, String content) {
        return  updatePostService.execute(userId, postId, content);
    }

    public PostResponseDTO deletePost(Long userId, Long postId) {
        return deletePostService.execute(userId, postId);
    }

    public boolean toggleLike(Long postId, Long userId) {
        return toggleLikeService.execute(postId, userId);
    }
}
