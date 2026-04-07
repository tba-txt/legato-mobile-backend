package com.floriano.legato_api.services.SwipeService;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.model.Chat.Chat;
import com.floriano.legato_api.model.Notification.enums.NotificationTargetType;
import com.floriano.legato_api.model.Notification.enums.NotificationType;
import com.floriano.legato_api.model.Swipe.Swipe;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.SwipeRepository;
import com.floriano.legato_api.services.ChatService.ChatService;
import com.floriano.legato_api.services.NotificationService.NotificationService;
import com.floriano.legato_api.services.UserSevice.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessSwipeService {

    private final SwipeRepository swipeRepository;
    private final UserService userService;
    private final ChatService chatService;
    private final NotificationService notificationService;

    @Transactional
    public Chat execute(Long swiperId, Long swipedId, boolean isLike) {
        User swiper = userService.findById(swiperId);
        User swiped = userService.findById(swipedId);

        Swipe swipe = new Swipe();
        swipe.setSwiper(swiper);
        swipe.setSwiped(swiped);
        swipe.setLike(isLike);
        swipeRepository.save(swipe);

        if (!isLike) return null;

        boolean hasMatch = swipeRepository.findBySwiperAndSwipedAndIsLikeTrue(swiped, swiper).isPresent();

        if (hasMatch) {
            Chat chat = chatService.getOrCreateChatBetween(swiper, swiped);

            // Notifica quem recebeu o like final
            NotificationRequestDTO toSwiped = new NotificationRequestDTO();
            toSwiped.setSenderId(swiperId);
            toSwiped.setRecipientId(swipedId);
            toSwiped.setTitle("Novo Match!");
            toSwiped.setMessage("Vocês deram match! Comece uma conversa.");
            toSwiped.setType(NotificationType.MATCH);
            toSwiped.setTargetType(NotificationTargetType.CHAT);
            toSwiped.setTargetId(chat.getId()); // O ID da conversa pro front redirecionar

            // Notifica quem deu o último like
            NotificationRequestDTO toSwiper = new NotificationRequestDTO();
            toSwiper.setSenderId(swipedId);
            toSwiper.setRecipientId(swiperId);
            toSwiper.setTitle("Novo Match!");
            toSwiper.setMessage("Vocês deram match! Comece uma conversa.");
            toSwiper.setType(NotificationType.MATCH);
            toSwiper.setTargetType(NotificationTargetType.CHAT);
            toSwiper.setTargetId(chat.getId()); 

            notificationService.createNotification(toSwiped);
            notificationService.createNotification(toSwiper);
            
            return chat; // Retorna o chat criado
        }

        return null;
    }
}