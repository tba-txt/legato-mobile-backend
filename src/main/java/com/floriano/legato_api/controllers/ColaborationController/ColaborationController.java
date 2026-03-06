package com.floriano.legato_api.controllers.ColaborationController;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationRequestDTO;
import com.floriano.legato_api.dto.ColaborationDTO.ColaborationResponseDTO;
import com.floriano.legato_api.dto.ColaborationDTO.ColaborationUpdateDTO;
import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.payload.ApiResponse;
import com.floriano.legato_api.payload.ResponseFactory;
import com.floriano.legato_api.services.ColaborationService.ColaborationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("colab")
@Tag(name = "Collaborations")
@RequiredArgsConstructor
public class ColaborationController {

    private final ColaborationService colaborationService;

    @Operation(
            summary = "Listar colaborações",
            description = "Permite que o usuário autenticado liste  colaborações. "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ColaborationResponseDTO>>> listColaboration() {
        List<ColaborationResponseDTO> responseDTO = colaborationService.listColaborations();

        return ResponseFactory.ok("Colaborações recuperadas com sucesso!", responseDTO);
    }

    @Operation(
            summary = "Listar colaborações do usuário",
            description = "Permite que o usuário autenticado liste suas colaborações. "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<ColaborationResponseDTO>>> listColaborationUsers(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        List<ColaborationResponseDTO> responseDTO = colaborationService.listByUserColaboration(userId);

        return ResponseFactory.ok("Colaborações recuperadas com sucesso!", responseDTO);
    }

    @Operation(
            summary = "Criar colaboração",
            description = "Permite que o usuário autenticado crie uma colaboração. "
                    + "O ID do criador é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @PostMapping
    public ResponseEntity<ApiResponse<ColaborationResponseDTO>> createColaboration(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                   @RequestBody ColaborationRequestDTO dto) {
        Long userId = userPrincipal.getUser().getId();
        ColaborationResponseDTO responseDTO = colaborationService.createColaboration(userId, dto);

        return ResponseFactory.ok("Colaboração criada com sucesso!", responseDTO);
    }

    @Operation(
            summary = "Atualizar imagem da colaboração",
            description = "Permite que o usuário autenticado atualize uma imagem da colaboração. "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @PutMapping("/{id}/upload-image")
    public ResponseEntity<ApiResponse<ColaborationResponseDTO>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        var response = colaborationService.updateColaborationimage(id, file);
        return ResponseFactory.ok("Imagem atualizada!", response);
    }


    @Operation(
            summary = "Atualizar colaboração",
            description = "Permite que o usuário autenticado atualize uma colaboração. "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @PutMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<ColaborationResponseDTO>> updateColaboration(@PathVariable Long id,
                                                                                   @RequestBody ColaborationUpdateDTO dto) {
        ColaborationResponseDTO responseDTO = colaborationService.updateColaboration(id, dto);

        return ResponseFactory.ok("Colaboração atualizda com sucesso!", responseDTO);
    }

    @Operation(
            summary = "Deletar colaboração",
            description = "Permite que o usuário autenticado delete uma colaboração. "
                    + "O ID do usuário é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<Void>> DeleteColaboration(@PathVariable Long id) {
        colaborationService.deleteColaboration(id);

        return ResponseFactory.ok("Colaboração deletada com sucesso!");
    }
}
