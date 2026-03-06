package com.floriano.legato_api.dto.AuthDTO;

import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UserResponseDTO user;
}