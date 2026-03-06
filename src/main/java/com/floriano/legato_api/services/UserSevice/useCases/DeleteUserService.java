package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.UserNotFoundException;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.UserSevice.utils.UserDeleteHelper;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserService {

    private UserRepository userRepository;
    private UserDeleteHelper userDeleteHelper;

    public DeleteUserService(UserRepository userRepository, UserDeleteHelper userDeleteHelper) {
        this.userRepository = userRepository;
        this.userDeleteHelper = userDeleteHelper;
    }

    public void execute(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id " + id + " não encontrado"));

        userDeleteHelper.deleteUserAndCleanup(user);
    }
}
