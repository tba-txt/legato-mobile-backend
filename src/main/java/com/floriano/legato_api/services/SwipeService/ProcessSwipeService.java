package com.floriano.legato_api.services.SwipeService;

import com.floriano.legato_api.dto.NotificationDTO.NotificationRequestDTO;
import com.floriano.legato_api.dto.SwipeDTO.SwipeHistoryResponseDTO;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessSwipeService {

    private final SwipeRepository swipeRepository;
    private final UserService userService;
    private final ChatService chatService;
    private final NotificationService notificationService;

    public List<SwipeHistoryResponseDTO> getSwipeHistory(Long swiperId) {
        List<Swipe> swipes = swipeRepository.findBySwiperIdOrderByCreatedAtDesc(swiperId);

        return swipes.stream()
            .map(swipe -> {
                User musician = swipe.getSwiped();
                return SwipeHistoryResponseDTO.builder()
                    .id(musician.getId())
                    .displayName(musician.getDisplayName() != null ? musician.getDisplayName() : musician.getUsername())
                    .profilePicture(musician.getProfilePicture())
                    .instruments(musician.getInstruments())
                    .genres(musician.getGenres())
                    .bio(musician.getBio())
                    .photosCard(musician.getPhotosCard())
                    .direction(swipe.isLike() ? "like" : "dislike")
                    .createdAt(swipe.getCreatedAt())
                    .build();
            })
            .toList();
    }

    @Transactional
    public Chat execute(Long swiperId, Long swipedId, boolean isLike) {
        User swiper = userService.findById(swiperId);
        User swiped = userService.findById(swipedId);

        if (swipeRepository.existsBySwiperAndSwiped(swiper, swiped)) {
            return null; 
        }

        Swipe swipe = new Swipe();
        swipe.setSwiper(swiper);
        swipe.setSwiped(swiped);
        swipe.setLike(isLike);
        swipeRepository.save(swipe);

        if (!isLike) return null;

        boolean hasMatch = swipeRepository.existsBySwiperAndSwipedAndIsLikeTrue(swiped, swiper);

        if (hasMatch) {
            Chat chat = chatService.getOrCreateChatBetween(swiper, swiped);

            NotificationRequestDTO toSwiped = new NotificationRequestDTO();
            toSwiped.setSenderId(swiperId);
            toSwiped.setRecipientId(swipedId);
            toSwiped.setTitle("Novo Match!");
            toSwiped.setMessage("Vocês deram match! Comece uma conversa.");
            toSwiped.setType(NotificationType.MATCH);
            toSwiped.setTargetType(NotificationTargetType.CHAT);
            toSwiped.setTargetId(chat.getId());

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
            
            return chat;
        }

        return null;
    }
}