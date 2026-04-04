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
    public boolean execute(Long swiperId, Long swipedId, boolean isLike) {
        User swiper = userService.findById(swiperId);
        User swiped = userService.findById(swipedId);

        Swipe swipe = new Swipe();
        swipe.setSwiper(swiper);
        swipe.setSwiped(swiped);
        swipe.setLike(isLike);
        swipeRepository.save(swipe);

        if (!isLike) return false;

        boolean hasMatch = swipeRepository.findBySwiperAndSwipedAndIsLikeTrue(swiped, swiper).isPresent();

        if (hasMatch) {
            // 1. Cria o Chat
            Chat chat = chatService.getOrCreateChatBetween(swiper, swiped);

            // 2. Dispara notificações para os dois usuários usando o DTO esperado pelo serviço
            NotificationRequestDTO toSwiped = new NotificationRequestDTO();
            toSwiped.setSenderId(swiperId);
            toSwiped.setRecipientId(swipedId);
            toSwiped.setTitle("Novo Match");
            toSwiped.setMessage("Você deu match com " + swiper.getUsername());
            toSwiped.setType(NotificationType.LIKE);
            toSwiped.setTargetType(NotificationTargetType.USER);
            toSwiped.setTargetId(swiperId);

            NotificationRequestDTO toSwiper = new NotificationRequestDTO();
            toSwiper.setSenderId(swipedId);
            toSwiper.setRecipientId(swiperId);
            toSwiper.setTitle("Novo Match");
            toSwiper.setMessage("Você deu match com " + swiped.getUsername());
            toSwiper.setType(NotificationType.LIKE);
            toSwiper.setTargetType(NotificationTargetType.USER);
            toSwiper.setTargetId(swipedId);

            notificationService.createNotification(toSwiped);
            notificationService.createNotification(toSwiper);
            
            return true;
        }

        return false;
    }
}