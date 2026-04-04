package com.floriano.legato_api.controllers.SwipeController;

import com.floriano.legato_api.dto.SwipeDTO.SwipeRequestDTO;
import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.payload.ApiResponse;
import com.floriano.legato_api.payload.ResponseFactory;
import com.floriano.legato_api.services.SwipeService.ProcessSwipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swipes")
@Tag(name = "Swipe/Match", description = "Endpoints para dar Like/Pass em usuários")
@RequiredArgsConstructor
public class SwipeController {

    private final ProcessSwipeService processSwipeService;

    @Operation(
        summary = "Dar um Like ou Dislike", 
        description = "Registra a ação do usuário na tela de discovery. Se for Like e o outro já tiver curtido, gera Match e Chat.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> processSwipe(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody SwipeRequestDTO requestDTO) {

        Long swiperId = userPrincipal.getUser().getId();
        
        // Impede que o usuário dê like nele mesmo, caso o front mande errado
        if (swiperId.equals(requestDTO.getSwipedId())) {
            return ResponseFactory.badRequest("Você não pode curtir o seu próprio perfil.");
        }

        boolean hasMatch = processSwipeService.execute(swiperId, requestDTO.getSwipedId(), requestDTO.isLike());

        String message = hasMatch ? "MATCH! Você e " + requestDTO.getSwipedId() + " se curtiram." : "Swipe registrado com sucesso.";
        
        // Retorna a boolean 'hasMatch' para o Front-end saber se joga confete na tela ou não
        return ResponseFactory.ok(message, hasMatch); 
    }
}