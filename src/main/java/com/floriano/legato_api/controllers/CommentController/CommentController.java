package com.floriano.legato_api.controllers.CommentController;

import com.floriano.legato_api.dto.CommentDTO.CommentRequestDTO;
import com.floriano.legato_api.dto.CommentDTO.CommentResponseDTO;
import com.floriano.legato_api.dto.PostDTO.PostUpdateDTO;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.payload.ApiResponse;
import com.floriano.legato_api.payload.ResponseFactory;
import com.floriano.legato_api.services.CommentService.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comments")
@Tag(name = "Comments", description = "Endpoints related to comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Listar comentários do usuário",
            description = "Permite que o usuário autenticado visualize seus comentários. "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", 
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<CommentResponseDTO>>> listUserComments(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        List<CommentResponseDTO> responseDTO = commentService.listUserComments(userId);

        return ResponseFactory.ok("Comentários recuperados com sucesso!", responseDTO);
    }

    @Operation(
            summary = "Criar comentário",
            description = "Permite que o usuário autenticado comente em uma publicação. "
                    + "O ID do criador é obtido automaticamente a partir do token JWT.", 
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping()
    public ResponseEntity<ApiResponse<CommentResponseDTO>> createComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDTO dto) {
        
        Long userId = userPrincipal.getUser().getId();
        CommentResponseDTO responseDTO = commentService.createComment(userId, dto);

        return ResponseFactory.ok("Comentário publicado com sucesso!", responseDTO);
    }

    @Operation(
            summary = "Atualizar um comentário",
            description = "Atualiza o conteúdo de um comentário específico usando o ID do comentário. "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", 
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long commentId,
            @RequestBody PostUpdateDTO dto 
    ) {
        Long userId = userPrincipal.getUser().getId();

        CommentResponseDTO response = commentService.updateComment(userId, commentId, dto.content());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deletar um comentário",
            description = "Deleta um comentário específico usando o ID do comentário. "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", 
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> deleteComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long commentId
    ) {
        Long userId = userPrincipal.getUser().getId();

        CommentResponseDTO response = commentService.deleteComment(userId, commentId);
        return ResponseEntity.ok(response);
    }
}