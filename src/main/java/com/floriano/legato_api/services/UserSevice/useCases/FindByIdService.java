package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FindByIdService {

    private final UserRepository userRepository;

    public FindByIdService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id " + id + " não encontrado"));
    }
}
