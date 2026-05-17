package com.floriano.legato_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthHandshakeInterceptor authHandshakeInterceptor;
    private final UserHandshakeHandler userHandshakeHandler;

    public WebSocketConfig(AuthHandshakeInterceptor authHandshakeInterceptor, UserHandshakeHandler userHandshakeHandler) {
        this.authHandshakeInterceptor = authHandshakeInterceptor;
        this.userHandshakeHandler = userHandshakeHandler;
    }

    // 1. Cria um agendador exclusivo para o Heartbeat não travar a aplicação
    @Bean
    public TaskScheduler heartbeatTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("ws-heartbeat-");
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue")
                .setTaskScheduler(heartbeatTaskScheduler()) // 2. Acopla o agendador
                .setHeartbeatValue(new long[]{15000, 15000}); // 3. Ping do Back (15s) e Ping esperado do Front (15s)
                
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .addInterceptors(authHandshakeInterceptor)
                .setHandshakeHandler(userHandshakeHandler)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}