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
import com.floriano.legato_api.model.User.enums.InstrumentList;
import com.floriano.legato_api.model.User.enums.Genre;
import com.floriano.legato_api.model.User.enums.UserSex;
import com.floriano.legato_api.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Pattern;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RecaptchaService recaptchaService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender; // Injetando o disparador de e-mails

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AutheticationDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        try {
            var auth = authenticationManager.authenticate(usernamePassword);
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            User user = userPrincipal.getUser();

            // REGRA DE SEGURANÇA 1: E-mail não verificado
            if (!user.isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Por favor, verifique seu e-mail antes de fazer login.", null));
            }

            // REGRA DE SEGURANÇA 2: Senha expirada (mais de 90 dias)
            if (user.getLastPasswordChange() != null && user.getLastPasswordChange().plusDays(90).isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Sua senha expirou por segurança (90 dias). Use a opção 'Esqueci a senha' para atualizá-la.", null));
            }

            var token = tokenService.generateToken(user);
            var userDTO = UserMapper.toDTO(user);
            var response = new AuthResponseDTO(token, userDTO);
            return ResponseFactory.ok("Login realizado com sucesso", response);

        } catch (DisabledException | InternalAuthenticationServiceException e) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN)
                     .body(new ApiResponse<>(false, "Conta desativada ou com problemas. Contate o suporte.", null));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Email ou senha incorretos.", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto data) {
        try {
            if (this.userRepository.findByEmail(data.email()).isPresent()) {
                return ResponseFactory.badRequest("Email already exists");
            }
            if (data.birthDate() == null || Period.between(data.birthDate(), LocalDate.now()).getYears() < 18) {
                return ResponseFactory.badRequest("Você precisa ter pelo menos 18 anos.");
            }

            String encryptedPassword = passwordEncoder.encode(data.password());
            User newUser = new User(data.email(), encryptedPassword, data.role(), data.username(), data.displayName());
            newUser.setBirthDate(data.birthDate());
            newUser.setBio(data.bio());
            newUser.setObjective(data.objective());
            
            if (data.sex() != null && !data.sex().isBlank()) newUser.setSex(UserSex.valueOf(data.sex().toUpperCase()));
            if (data.instruments() != null && !data.instruments().isEmpty()) {
                newUser.setInstruments(data.instruments().stream().map(i -> InstrumentList.valueOf(i.toUpperCase())).toList());
            }
            if (data.genres() != null && !data.genres().isEmpty()) {
                newUser.setGenres(data.genres().stream().map(g -> Genre.valueOf(g.toUpperCase())).toList());
            }
            if (data.links() != null) {
                newUser.setSpotify(data.links().getSpotify());
                newUser.setSoundcloud(data.links().getSoundcloud());
                newUser.setInstagram(data.links().getInstagram());
                newUser.setYoutube(data.links().getYoutube());
                newUser.setWebsite(data.links().getWebsite());
            }

            // GERA TOKEN DE VERIFICAÇÃO DE EMAIL
            String verifyToken = UUID.randomUUID().toString();
            newUser.setEmailVerificationToken(verifyToken);
            this.userRepository.save(newUser);

            // SIMULAÇÃO DE ENVIO DE E-MAIL E LOG NO CONSOLE PARA TESTES RÁPIDOS
            String link = "http://localhost:8081/auth/verify-email?token=" + verifyToken;
            enviarEmail(newUser.getEmail(), "Confirme sua conta no Legato", "Clique aqui: " + link);
            
            return ResponseFactory.ok("Usuário cadastrado! Verifique seu console/e-mail para confirmar a conta antes de logar.", null);

        } catch (Exception e) {
            return ResponseFactory.badRequest("Error: " + e.getMessage());
        }
    }

    // --- NOVAS ROTAS DE SEGURANÇA EXIGIDAS ---

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado."));
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        return ResponseFactory.ok("E-mail verificado com sucesso! Você já pode fazer login.", null);
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            String resetToken = UUID.randomUUID().toString();
            user.setPasswordResetToken(resetToken);
            userRepository.save(user);

            // AGORA O LINK É DINÂMICO
            String link = baseUrl + "/auth/reset-password?token=" + resetToken;
            
            enviarEmail(email, "Recuperação de Senha Legato", "Acesse este link para resetar sua senha: " + link);
        }
        return ResponseFactory.ok("Se o e-mail existir, as instruções foram enviadas.", null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token de recuperação inválido."));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setLastPasswordChange(LocalDateTime.now()); // Zera o contador de 90 dias
        userRepository.save(user);

        return ResponseFactory.ok("Senha alterada com sucesso! Você já pode fazer login.", null);
    }

    // Método auxiliar para disparar e-mails de verdade depois (descomentar quando configurar o SMTP)
    private void enviarEmail(String para, String assunto, String texto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("legatoapi@gmail.com");
            message.setTo(para);
            message.setSubject(assunto);
            message.setText(texto);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}