package com.floriano.legato_api.config;

import com.floriano.legato_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserRepository userRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = accessor.getUser() != null ? accessor.getUser().getName() : null;

        if (username != null) {
            userRepository.findByEmail(username).ifPresent(user -> {
                user.setIsOnline(true);
                user.setLastSeen(LocalDateTime.now());
                userRepository.save(user);
            });
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = accessor.getUser() != null ? accessor.getUser().getName() : null;

        if (username != null) {
            userRepository.findByEmail(username).ifPresent(user -> {
                user.setIsOnline(false);
                user.setLastSeen(LocalDateTime.now());
                userRepository.save(user);
            });
        }
    }
}