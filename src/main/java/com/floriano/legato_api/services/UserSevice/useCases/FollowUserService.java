package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.Notification.enums.NotificationTargetType;
import com.floriano.legato_api.model.Notification.enums.NotificationType;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.NotificationService.useCases.CreateNotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowUserService {

    private final UserRepository userRepository;
    private final CreateNotificationService createNotificationService;

    @Transactional
    public UserResponseDTO execute(Long followerId, Long targetId) {

        if (followerId.equals(targetId)) {
            throw new IllegalArgumentException("Um usuário não pode seguir a si mesmo.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuário seguidor com id " + followerId + " não encontrado"));

        User target = userRepository.findById(targetId)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuário alvo com id " + targetId + " não encontrado"));

        if (follower.getBlockedUsers().contains(target) ||
                target.getBlockedUsers().contains(follower)) {
            throw new IllegalStateException("Ação não permitida: um dos usuários bloqueou o outro.");
        }

        follower.follow(target);
        userRepository.save(follower);
        userRepository.save(target);

        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setSenderId(follower.getId());
        dto.setRecipientId(target.getId());
        dto.setMessage(follower.getUsername() + " começou a seguir você.");
        dto.setType(NotificationType.FOLLOW);
        dto.setTargetType(NotificationTargetType.USER);
        dto.setTargetId(follower.getId());
        dto.setCreatedAt(null);

        createNotificationService.execute(dto);

        return UserMapper.toDTO(target);
    }
}

