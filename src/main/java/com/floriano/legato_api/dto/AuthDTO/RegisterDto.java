package com.floriano.legato_api.dto.AuthDTO;

import com.floriano.legato_api.model.User.enums.UserRole;
import com.floriano.legato_api.dto.UserDTO.UserUpdateDTO.ExternalLinksDTO; // Reaproveitando do Update!
import java.time.LocalDate;
import java.util.List;

public record RegisterDto(
        String email, 
        String password, 
        UserRole role, 
        String username, 
        String displayName,
        LocalDate birthDate,
        String recaptchaToken,

        // --- CAMPOS NOVOS PARA O PERFIL INICIAL ---
        String bio,
        String objective,
        String sex,
        List<String> instruments,
        List<String> genres,
        ExternalLinksDTO links
) {}