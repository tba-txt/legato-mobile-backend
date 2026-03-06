package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.UserListDTO;
import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAllUsersService {

    private final UserRepository userRepository;

    public ListAllUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserListDTO> execute() {
        List<User> users = userRepository.findAll();
        List<UserListDTO> userResponseDTOS = users.stream().map(UserMapper:: toListDTO).toList();
        return userResponseDTOS;
    }

    public List<User> getRawUsers() {
        return userRepository.findAll();
    }
}
