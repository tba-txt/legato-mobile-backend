package com.floriano.legato_api.dto.AuthDTO;

import com.floriano.legato_api.model.User.enums.UserRole;
import java.time.LocalDate;

// Quando o frontend terminar os testes com usuarios fakes, descomente as imports e anotacoes abaixo.
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Pattern;
// import jakarta.validation.constraints.Size;

public record RegisterDto(
        // @NotBlank(message = "Email e obrigatorio")
        // @Email(message = "Email invalido")
        // @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email invalido")
        String email, 

        // @NotBlank(message = "Senha e obrigatoria")
        // @Pattern(
        //         regexp = "^(?=.*[A-Z])(?=.*[^\\w\\s]).{8,}$",
        //         message = "A senha deve ter no minimo 8 caracteres, 1 letra maiuscula e 1 caractere especial"
        // )
        String password, 

        // @NotNull(message = "Role e obrigatoria")
        UserRole role, 

        // @NotBlank(message = "Username e obrigatorio")
        // @Size(min = 5, message = "Username deve ter no minimo 5 caracteres")
        String username, 

        // @NotBlank(message = "Display name e obrigatorio")
        // @Size(min = 5, message = "Display name deve ter no minimo 5 caracteres")
        String displayName,

        // @NotNull(message = "Data de nascimento e obrigatoria")
        LocalDate birthDate,

        // @NotBlank(message = "Token do recaptcha e obrigatorio")
        String recaptchaToken
) {}