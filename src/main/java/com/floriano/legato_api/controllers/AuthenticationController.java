package com.floriano.legato_api.controllers;

import com.floriano.legato_api.dto.AuthDTO.AuthResponseDTO;
import com.floriano.legato_api.dto.AuthDTO.AutheticationDto;
import com.floriano.legato_api.dto.AuthDTO.RegisterDto;
import com.floriano.legato_api.mapper.user.UserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.infra.security.TokenService;
import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.payload.ResponseFactory;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.AuthorizationService.RecaptchaService;

import java.time.LocalDate;
import java.time.Period;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.regex.Pattern;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthenticationController {

    // Regex prontas para validacoes de cadastro.
    // Mantidas aqui para facilitar ativacao futura sem alterar regra no frontend agora.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[^\\w\\s]).{8,}$");

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final UserRepository userRepository;

    private final RecaptchaService recaptchaService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AutheticationDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        boolean emailsExists = userRepository.existsByEmail(data.email());
        if (!emailsExists) throw new IllegalArgumentException("Email não encontrado!");
        var auth = authenticationManager.authenticate(usernamePassword);

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        User user = userPrincipal.getUser();

        var token = tokenService.generateToken(user);
        var userDTO = UserMapper.toDTO(user);
        var response = new AuthResponseDTO(token, userDTO);

        return ResponseFactory.ok("Login realizado com sucesso", response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto data) {
        try {
            /*
            // Ative este bloco quando quiser endurecer validacoes de cadastro.
            if (data.email() == null || data.email().isBlank() || !EMAIL_PATTERN.matcher(data.email()).matches()) {
                return ResponseFactory.badRequest("Email invalido.");
            }

            if (data.password() == null || data.password().isBlank() || !PASSWORD_PATTERN.matcher(data.password()).matches()) {
                return ResponseFactory.badRequest("A senha deve ter no minimo 8 caracteres, 1 letra maiuscula e 1 caractere especial.");
            }

            if (data.username() == null || data.username().trim().length() < 5) {
                return ResponseFactory.badRequest("Username deve ter no minimo 5 caracteres.");
            }

            if (data.displayName() == null || data.displayName().trim().length() < 5) {
                return ResponseFactory.badRequest("Display name deve ter no minimo 5 caracteres.");
            }
            */

            boolean isHuman = recaptchaService.validateToken(data.recaptchaToken());
            
           /* if (!isHuman) {[
           ]
                return ResponseFactory.forbidden("Falha na validação do reCAPTCHA. Você é um robô?");
            } */ //comentado para facilitar os testes, mas deve ser descomentado para produção

            if (this.userRepository.findByEmail(data.email()).isPresent()) {
                return ResponseFactory.badRequest("Email already exists");
            }

            if (data.birthDate() == null) {
                return ResponseFactory.badRequest("A data de nascimento é obrigatória.");
            }

            int age = Period.between(data.birthDate(), LocalDate.now()).getYears();

            if (age < 18) {
                return ResponseFactory.badRequest("Você precisa ter pelo menos 18 anos para se cadastrar no Legato.");
            }

            String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
            
            User newUser = new User(data.email(), encryptedPassword, data.role(), data.username(), data.displayName());
            newUser.setBirthDate(data.birthDate());

            this.userRepository.save(newUser);

            var token = tokenService.generateToken(newUser);
            var userDTO = UserMapper.toDTO(newUser);
            var response = new AuthResponseDTO(token, userDTO);

            return ResponseFactory.ok("Usuário cadastrado com sucesso!", response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseFactory.badRequest("Error: " + e.getMessage());
        }
    }
}
