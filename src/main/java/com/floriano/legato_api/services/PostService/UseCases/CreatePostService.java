package com.floriano.legato_api.services.PostService.UseCases;

import com.floriano.legato_api.dto.PostDTO.PostRequestDTO;
import com.floriano.legato_api.dto.PostDTO.PostResponseDTO;
import com.floriano.legato_api.mapper.PostMapper.PostMapper;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.Post.TypeMedia;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.PostRepository;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.CloudinaryService.CloudinaryService;
import com.floriano.legato_api.services.PostService.Utils.DetermineMediaType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class CreatePostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CloudinaryService cloudinaryService;

    public PostResponseDTO execute(Long userId, String content, MultipartFile mediaFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String mediaUrl = null;
        TypeMedia typeMedia = null;

        if (mediaFile != null && !mediaFile.isEmpty()) {
            mediaUrl = cloudinaryService.uploadFile(mediaFile, "posts");
            typeMedia = DetermineMediaType.determineMediaType(mediaFile);
        }

        Post post = new Post();
        post.setContent(content);
        post.setUrlMedia(mediaUrl);
        post.setTypeMedia(typeMedia);

        post.assignUser(user);

        postRepository.save(post);

        return PostMapper.toResponse(post);
    }
}
