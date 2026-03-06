package com.floriano.legato_api.config;

import com.floriano.legato_api.infra.security.TokenService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        var query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            System.out.println("[WS] Token ausente na query");
            return false;
        }

        var token = query.substring(query.indexOf("token=") + 6);

        var subject = tokenService.validateToken(token);

        if (subject == null || subject.isEmpty()) {
            System.out.println("[WS] Token inválido");
            return false;
        }

        attributes.put("userEmail", subject);
        System.out.println("[WS] Conexão autenticada de: " + subject);

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
    }
}
