package com.floriano.legato_api.services.SwipeService;

import com.floriano.legato_api.model.Chat.Chat;
import com.floriano.legato_api.model.Swipe.Swipe;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.SwipeRepository;
import com.floriano.legato_api.services.ChatService.ChatService;
import com.floriano.legato_api.services.UserSevice.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessSwipeServiceTest {

    @Mock
    private SwipeRepository swipeRepository;
    @Mock
    private UserService userService;
    @Mock
    private ChatService chatService;
    @InjectMocks
    private ProcessSwipeService processSwipeService;

    @Test
    void execute_Like_NoMatch() {
        Long swiperId = 1L;
        Long targetId = 2L;
        User swiper = new User();
        swiper.setId(swiperId);
        User target = new User();
        target.setId(targetId);

        when(userService.findById(swiperId)).thenReturn(swiper);
        when(userService.findById(targetId)).thenReturn(target);
        when(swipeRepository.existsBySwiperAndTargetAndLiked(target, swiper, true)).thenReturn(false);

        Chat result = processSwipeService.execute(swiperId, targetId, true);

        assertNull(result);
        verify(swipeRepository).save(any(Swipe.class));
        verify(chatService, never()).getOrCreateChatBetween(any(), any());
    }

    @Test
    void execute_Like_IsMatch() {
        Long swiperId = 1L;
        Long targetId = 2L;
        User swiper = new User();
        swiper.setId(swiperId);
        User target = new User();
        target.setId(targetId);
        Chat chat = new Chat();

        when(userService.findById(swiperId)).thenReturn(swiper);
        when(userService.findById(targetId)).thenReturn(target);
        when(swipeRepository.existsBySwiperAndTargetAndLiked(target, swiper, true)).thenReturn(true);
        when(chatService.getOrCreateChatBetween(swiper, target)).thenReturn(chat);

        Chat result = processSwipeService.execute(swiperId, targetId, true);

        assertNotNull(result);
        assertEquals(chat, result);
        verify(swipeRepository).save(any(Swipe.class));
    }

    @Test
    void execute_Dislike() {
        Long swiperId = 1L;
        Long targetId = 2L;
        User swiper = new User();
        swiper.setId(swiperId);
        User target = new User();
        target.setId(targetId);

        when(userService.findById(swiperId)).thenReturn(swiper);
        when(userService.findById(targetId)).thenReturn(target);

        Chat result = processSwipeService.execute(swiperId, targetId, false);

        assertNull(result);
        verify(swipeRepository).save(any(Swipe.class));
        verify(chatService, never()).getOrCreateChatBetween(any(), any());
    }
}
