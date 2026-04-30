package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserService {

    private final UserRepository userRepository;

    @Transactional
    public void execute(String email) { 
        User user = userRepository.findByEmail(email) 
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Lógica de Soft Delete
        user.setActive(false);
        
        user.setDisplayName("Usuário Excluído");
        user.setProfilePicture(null); 
        user.setBio(null);
        
        userRepository.save(user);
    }
}