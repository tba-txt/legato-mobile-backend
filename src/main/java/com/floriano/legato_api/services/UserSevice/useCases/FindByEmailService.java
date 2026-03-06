package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FindByEmailService {

    private final UserRepository userRepository;

    public FindByEmailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário com e-mail " + email + " não encontrado"));
    }
}
