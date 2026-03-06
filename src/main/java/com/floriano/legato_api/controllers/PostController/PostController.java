package com.floriano.legato_api.controllers.PostController;

import com.floriano.legato_api.dto.PostDTO.PostRequestDTO;
import com.floriano.legato_api.dto.PostDTO.PostResponseDTO;
import com.floriano.legato_api.dto.PostDTO.PostUpdateDTO;
import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.services.PostService.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("posts")
@AllArgsConstructor
@Tag(name = "Posts")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Criar Post",
            description = "Permite que o usuário crie um post em seu perfil "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostResponseDTO> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart(value = "content", required = false) String content,
            @RequestPart(value = "media", required = false) MultipartFile media
    ) {
        User authenticatedUser = userPrincipal.getUser();
        Long id = authenticatedUser.getId();

        PostResponseDTO response = postService.createPost(id, content, media);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar Posts",
            description = "Lista os posts de um usuário "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> listPosts(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User authenticatedUser = userPrincipal.getUser();
        Long id = authenticatedUser.getId();

        List<PostResponseDTO> response = postService.listPosts(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar um post",
            description = "Atualiza o post de um usuário "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @RequestBody PostUpdateDTO dto
    ) {
        User authenticatedUser = userPrincipal.getUser();
        Long userId = authenticatedUser.getId();

        PostResponseDTO response = postService.updatePostService(userId, postId, dto.content());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deletar um Post",
            description = "Deletar um Post de um usuário "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId
    ) {
        User authenticatedUser = userPrincipal.getUser();
        Long userId = authenticatedUser.getId();

        PostResponseDTO response = postService.deletePost(userId, postId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Curtir/descurtir um Post",
            description = "Usuário curtindo/descurtindo um Post "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Map<String, Boolean>> likeDislikePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId
    ) {
        User authenticatedUser = userPrincipal.getUser();
        Long userId = authenticatedUser.getId();

        Boolean liked = postService.toggleLike(postId , userId);

        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
