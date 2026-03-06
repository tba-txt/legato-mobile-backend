package com.floriano.legato_api.services.UserSevice.utils;


import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UserDeleteHelper {

    private final UserRepository userRepository;

    public UserDeleteHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void deleteUserAndCleanup(User user) {
        if (user == null) return;

        // SEGUIDORES / SEGUINDO
        for (User follower : user.getFollowers()) {
            follower.getFollowing().remove(user);
        }
        user.getFollowers().clear();

        for (User following : user.getFollowing()) {
            following.getFollowers().remove(user);
        }
        user.getFollowing().clear();

        // CONEXÕES
        for (User connection : user.getConnections()) {
            connection.getConnections().remove(user);
        }
        user.getConnections().clear();

        // BLOQUEADOS
        for (User blocked : user.getBlockedUsers()) {
            blocked.getBlockedUsers().remove(user);
        }
        user.getBlockedUsers().clear();

        //  PEDIDOS DE CONEXÃO
        user.getSentRequests().clear();
        user.getReceivedRequests().clear();

        // POSTS
        if (user.getPosts() != null) {
            user.getPosts().clear();
        }

        // LINKS E LOCALIZAÇÃO
        user.setLinks(null);
        user.setLocation(null);

        // REMOVER DO REPOSITÓRIO
        userRepository.delete(user);
    }
}
