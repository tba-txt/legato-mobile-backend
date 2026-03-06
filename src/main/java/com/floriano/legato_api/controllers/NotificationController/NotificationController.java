package com.floriano.legato_api.controllers.NotificationController;

import com.floriano.legato_api.dto.NotificationDTO.NotificationResponseDTO;
import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.payload.ApiResponse;
import com.floriano.legato_api.payload.ResponseFactory;
import com.floriano.legato_api.services.NotificationService.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("notifications")
@Tag(name = "Notifications", description = "Operations related to user notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "List notifications of authenticated user",
            description = "Returns all notifications belonging to the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> listNotificationsByUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long receiverId = userPrincipal.getUser().getId();
        List<NotificationResponseDTO> notifications =
                notificationService.findAllByRecipient(receiverId);

        return ResponseFactory.ok("Notifications retrieved successfully", notifications);
    }

    @Operation(
            summary = "Delete a notification by ID",
            description = "Deletes a specific notification belonging to the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        notificationService.deleteNotification(id, userPrincipal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all notifications of the authenticated user as read.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        notificationService.markAllAsRead(userPrincipal.getUser().getId());
        return ResponseEntity.ok().build();
    }

}
