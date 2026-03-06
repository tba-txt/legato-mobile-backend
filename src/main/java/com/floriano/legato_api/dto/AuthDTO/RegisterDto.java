package com.floriano.legato_api.dto.AuthDTO;

import com.floriano.legato_api.model.User.enums.UserRole;

public record RegisterDto(String email, String password, UserRole role, String username, String displayName) {
}
