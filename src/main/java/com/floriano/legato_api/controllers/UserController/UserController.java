package com.floriano.legato_api.controllers.UserController;

import com.floriano.legato_api.dto.ConnectionDTO.ConnectionRequestResponseDTO;
import com.floriano.legato_api.dto.UserDTO.*;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.Connection.ConnectionRequest;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.payload.ApiResponse;
import com.floriano.legato_api.payload.ResponseFactory;
import com.floriano.legato_api.services.UserSevice.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("users")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user by id", description = "Returns the user by id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long id = userPrincipal.getUser().getId();
        User user = userService.findById(id);
        return ResponseFactory.ok("Usuário recuperada com sucesso!", UserMapper.toDTO(user));
    }

    @Operation(summary = "Get user by username", description = "Returns the user by username", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(@PathVariable String username) {
        UserResponseDTO user = userService.findUserByUsername(username);
        return ResponseFactory.ok("Usuário recuperada com sucesso!", user);
    }

    @Operation(summary = "Get all users", description = "Returns the complete list of registered users", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserListDTO>>> getUsers() {
        List<UserListDTO> userResponseDTOList = userService.listAllUsers();
        return ResponseFactory.ok("Lista recuperada com sucesso!", userResponseDTOList);
    }

    @Operation(summary = "Get users location", description = "Returns the complete list of registered users by location", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/location")
    public ResponseEntity<List<LocationUserDTO>> LocationOfUsers(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "10") double radiusKm) {

        Long id = userPrincipal.getUser().getId();
        List<LocationUserDTO> result = userService.findLocationOfUsers(id, radiusKm);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update user location", description = "Updates the GPS coordinates of the authenticated user in real-time", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/location")
    public ResponseEntity<ApiResponse<Void>> updateLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody UpdateLocationRequestDTO data) {
        Long id = userPrincipal.getUser().getId();
        userService.updateUserLocation(id, data.latitude(), data.longitude());
        return ResponseFactory.ok("Localização atualizada com sucesso em tempo real!");
    }

    @Operation(summary = "Update user", description = "Updates an existing user by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,  @RequestBody UserUpdateDTO dto ) {
        Long id = userPrincipal.getUser().getId();
        UserResponseDTO responseDTO = userService.updateUser(id, dto);
        return ResponseFactory.ok("User updated successfully", responseDTO);
    }

    @Operation(summary = "Delete user", description = "Deletes a user by ID and cleans up related entities", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseFactory.ok("Usuário deletado com sucesso!");
    }

    @Operation(
            summary = "Seguir um usuário",
            description = "Permite que o usuário autenticado siga outro usuário. "
                    + "Requer autenticação via Bearer Token. ", security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/follow/{targetId}")
    public ResponseEntity<UserResponseDTO> followUser(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long targetId
    ) {
        User authenticatedUser = userPrincipal.getUser();
        Long followerId = authenticatedUser.getId();
        UserResponseDTO followed = userService.followUser(followerId, targetId);
        return ResponseEntity.ok(followed);
    }

    @Operation(
            summary = "Deixar de seguir um usuário",
            description = "Permite que o usuário autenticado deixe de seguir outro usuário. "
                    + "O ID do seguidor é obtido automaticamente a partir do token JWT.", security = { @SecurityRequirement(name = "bearerAuth") })
    @PostMapping("/unfollow/{targetId}")
    public ResponseEntity<UserResponseDTO> unfollowUser(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long targetId) {
        User authenticatedUser = userPrincipal.getUser();
        Long followerId = authenticatedUser.getId();
        UserResponseDTO unfollowed = userService.unfollowUser(followerId, targetId);
        return ResponseEntity.ok(unfollowed);
    }

    @Operation(
            summary = "Enviar pedido de conexão",
            description = "Permite que o usuário autenticado envie um pedido de conexão a outro usuário.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/connection-requests/{targetId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> sendConnectionRequest(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long targetId, @RequestParam(required = false) String message
    ) {
        Long senderId = userPrincipal.getUser().getId();
        UserResponseDTO responseDTO = userService.sendConnectionRequest(senderId, targetId, message);
        return ResponseFactory.ok("Pedido de conexão enviado com sucesso!", responseDTO);
    }

    @Operation(
            summary = "Aceitar pedido de conexão",
            description = "Permite que o usuário autenticado aceite um pedido de conexão recebido.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/connection-requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptConnectionRequest(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long requestId
    ) {
        Long receiverId = userPrincipal.getUser().getId();
        userService.acceptConnectionRequest(receiverId, requestId);
        return ResponseFactory.ok("Pedido de conexão aceito com sucesso!");
    }

    @Operation(
            summary = "Rejeitar pedido de conexão",
            description = "Permite que o usuário autenticado rejeite um pedido de conexão recebido.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/connection-requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectConnectionRequest(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long requestId
    ) {
        Long receiverId = userPrincipal.getUser().getId();
        userService.rejectConnectionRequest(receiverId, requestId);
        return ResponseFactory.ok("Pedido de conexão rejeitado com sucesso!");
    }

    @Operation(
            summary = "Listar conexões do usuário autenticado",
            description = "Retorna todos os usuários conectados ao usuário autenticado.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/connections")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> listConnections(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.getUser().getId();
        List<UserResponseDTO> connections = userService.listConnections(userId);
        return ResponseFactory.ok("Conexões recuperadas com sucesso!", connections);
    }

    @Operation(
            summary = "Listar seguidores do usuário autenticado",
            description = "Retorna todos os Seguidores ao usuário autenticado.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<UserListDTO>>> listFollowers(
            @PathVariable Long userId
    ) {
        List<UserListDTO> connections = userService.listFollowers(userId);
        return ResponseFactory.ok("Seguidores recuperadas com sucesso!", connections);
    }

    @Operation(
            summary = "Remover conexão existente",
            description = "Permite que o usuário autenticado remova uma conexão já existente entre dois usuários.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @DeleteMapping("/connections/{targetId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> removeConnection(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long targetId
    ) {
        Long userId = userPrincipal.getUser().getId();
        UserResponseDTO updated = userService.removeConnection(userId, targetId);
        return ResponseFactory.ok("Conexão removida com sucesso!", updated);
    }

    @Operation(
            summary = "Listar pedidos de conexão enviados",
            description = "Retorna todos os pedidos de conexão enviados pelo usuário autenticado.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/connection-requests/sent")
    public ResponseEntity<ApiResponse<List<ConnectionRequestResponseDTO>>> listSentRequests(@AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.getUser().getId();
        List<ConnectionRequestResponseDTO> sent = userService.listSentRequests(userId);
        return ResponseFactory.ok("Pedidos enviados recuperados com sucesso!", sent);
    }

    @Operation(
            summary = "Listar pedidos de conexão recebidos",
            description = "Retorna todos os pedidos de conexão recebidos pelo usuário autenticado.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/connection-requests/received")
    public ResponseEntity<ApiResponse<List<ConnectionRequestResponseDTO>>> listReceivedRequests(@AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.getUser().getId();
        List<ConnectionRequestResponseDTO> received = userService.listReceivedRequests(userId);
        return ResponseFactory.ok("Pedidos recebidos recuperados com sucesso!", received);
    }

    @Operation(
            summary = "Bloquear um usuário",
            description = "Permite que o usuário autenticado bloqueie outro usuário.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/block/{targetId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> blockUser(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long targetId
    ) {
        Long blockerId = userPrincipal.getUser().getId();
        UserResponseDTO blocked = userService.blockUser(blockerId, targetId);
        return ResponseFactory.ok("Usuário bloqueado com sucesso!", blocked);
    }

    @Operation(
            summary = "Desbloquear um usuário",
            description = "Permite que o usuário autenticado desbloqueie outro usuário.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/unblock/{targetId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> unblockUser(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long targetId
    ) {
        Long blockerId = userPrincipal.getUser().getId();
        UserResponseDTO unblocked = userService.unblockUser(blockerId, targetId);
        return ResponseFactory.ok("Usuário desbloqueado com sucesso!", unblocked);
    }

    @Operation(
            summary = "Listar usuários bloqueados",
            description = "Retorna todos os usuários bloqueados pelo usuário autenticado.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/blocked")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> listBlockedUsers(@AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.getUser().getId();
        List<UserResponseDTO> blocked = userService.listBlockedUsers(userId);
        return ResponseFactory.ok("Usuários bloqueados recuperados com sucesso!", blocked);
    }

    @Operation(
            summary = "Upload de imagem do usuário",
            description = """
                Permite que o usuário autenticado envie uma imagem para ser usada como **foto de perfil** ou **banner**.
                <br><br>
                Parâmetros aceitos:
                - `type`: tipo da imagem (`profile`, `banner` )
                - `file`: arquivo de imagem no formato `multipart/form-data`
                """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PutMapping("/upload-image")
    public ResponseEntity<ApiResponse<UserResponseDTO>> uploadUserImage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file
    ) {
        Long userId = userPrincipal.getUser().getId();
        UserResponseDTO responseDTO = userService.updateImg(userId, file, type);
        return ResponseFactory.ok("Usuário atualizado com sucesso!", responseDTO);
    }

    @Operation(
            summary = "Upload de imagens de card do usuário",
            description = """
                Parâmetros aceitos:
                - `type`: tipo da imagem (`profile`, `banner` )
                - `file`: arquivo de imagem no formato `multipart/form-data`
                """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PutMapping("/card-file")
    public ResponseEntity<ApiResponse<UserResponseDTO>> uploadUserCardImages(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("index") int index,
            @RequestParam("file") MultipartFile file 
    ) {
        Long userId = userPrincipal.getUser().getId();
        UserResponseDTO responseDTO = userService.updateCardImgs(userId, file, index);
        return ResponseFactory.ok("Usuário atualizado com sucesso!", responseDTO);
    }


    @DeleteMapping("/{id}/cards/{index}")
    public ResponseEntity<UserResponseDTO> removeCardImage(
            @PathVariable Long id,
            @PathVariable int index
    ) {
        return ResponseEntity.ok(userService.removeCardImage(id, index));
    }
}
