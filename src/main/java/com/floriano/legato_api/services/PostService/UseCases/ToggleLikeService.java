package com.floriano.legato_api.services.PostService.UseCases;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.exceptions.PostNotFoundException;
import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.model.Notification.enums.NotificationTargetType;
import com.floriano.legato_api.model.Notification.enums.NotificationType;
import com.floriano.legato_api.model.Post.Post;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.PostRepository;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.NotificationService.useCases.CreateNotificationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class ToggleLikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CreateNotificationService createNotificationService;

    @Transactional
    public boolean execute(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post não encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Set<User> likes = post.getLikes();

        boolean jaCurtiaAntes = likes.contains(user);
        boolean liked;

        if (jaCurtiaAntes) {
            likes.remove(user);
            liked = false;
        } else {
            likes.add(user);
            liked = true;
        }

        boolean isOwner = post.getUser().getId().equals(userId);

        if (liked && !jaCurtiaAntes && !isOwner) {

            NotificationRequestDTO dto = new NotificationRequestDTO();
            dto.setSenderId(user.getId());
            dto.setRecipientId(post.getUser().getId());
            dto.setMessage("@" + user.getUsername() + " curtiu seu post");
            dto.setType(NotificationType.LIKE);
            dto.setTargetType(NotificationTargetType.POST);
            dto.setTargetId(post.getId());
            dto.setCreatedAt(null);

            createNotificationService.execute(dto);
        }

        postRepository.save(post);
        return liked;
    }
}
