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
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Pattern;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

            if (!user.isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Por favor, verifique seu e-mail antes de fazer login.", null));
            }

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

            // Usa o baseUrl dinâmico (Render ou localhost:8082) em vez de chumbar a porta
            String link = baseUrl + "/auth/verify-email?token=" + verifyToken;
            
            // Monta um template HTML bonito
            String htmlMessage = "<div style=\"font-family: Arial, sans-serif; text-align: center; padding: 20px; color: #333;\">" +
                                 "<h2 style=\"color: #686AE7;\">Bem-vindo ao Legato!</h2>" +
                                 "<p>Falta pouco para você acessar nossa plataforma.</p>" +
                                 "<p>Para confirmar sua conta, clique no botão abaixo:</p>" +
                                 "<a href=\"" + link + "\" style=\"background-color: #686AE7; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 20px; font-weight: bold;\">Verificar Meu E-mail</a>" +
                                 "<p style=\"margin-top: 30px; font-size: 12px; color: #999;\">Se o botão não funcionar, copie e cole este link no navegador: <br>" + link + "</p>" +
                                 "</div>";

            enviarEmail(newUser.getEmail(), "Confirme sua conta no Legato", htmlMessage);
            
            return ResponseFactory.ok("Usuário cadastrado! Verifique seu e-mail para confirmar a conta antes de logar.", null);
        } catch (Exception e) {
            return ResponseFactory.badRequest("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    // --- NOVAS ROTAS DE SEGURANÇA EXIGIDAS ---

    @GetMapping(value = "/verify-email", produces = "text/html")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado."));
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);

        // Retorna uma página HTML bonita
        String htmlPage = "<!DOCTYPE html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Legato - Verificação</title></head>" +
                "<body style=\"font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f9; margin: 0;\">" +
                "<div style=\"text-align: center; background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">" +
                "<h1 style=\"color: #686AE7; margin-bottom: 10px;\"> E-mail Verificado!</h1>" +
                "<p style=\"color: #333; font-size: 18px;\">Sua conta no Legato foi ativada com sucesso.</p>" +
                "<p style=\"color: #666; margin-top: 20px;\">Você já pode fechar esta página e voltar para o aplicativo para fazer login.</p>" +
                "</div></body></html>";

        return ResponseEntity.ok(htmlPage);
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email.trim()).orElse(null);
        
        if (user != null) {
            String resetToken = UUID.randomUUID().toString();
            user.setPasswordResetToken(resetToken);
            userRepository.save(user);

            String link = baseUrl + "/auth/reset-password?token=" + resetToken;
            
            // Template HTML elegante e profissional
            String htmlMessage = "<div style=\"font-family: Arial, sans-serif; text-align: center; padding: 30px; color: #333; background-color: #f9f9f9; border-radius: 8px; max-width: 500px; margin: 0 auto;\">" +
                                 "<h2 style=\"color: #686AE7; margin-bottom: 10px;\">Recuperação de Senha</h2>" +
                                 "<p style=\"font-size: 16px; margin-bottom: 20px;\">Você solicitou a redefinição de senha para a sua conta no Legato.</p>" +
                                 "<p style=\"font-size: 14px; color: #666;\">Clique no botão abaixo para criar uma nova credencial de acesso:</p>" +
                                 "<a href=\"" + link + "\" style=\"background-color: #686AE7; color: white; padding: 14px 28px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 15px; margin-bottom: 25px; font-weight: bold; font-size: 16px; box-shadow: 0 2px 4px rgba(104,106,231,0.3);\">Redefinir Minha Senha</a>" +
                                 "<p style=\"font-size: 12px; color: #999; border-top: 1px solid #eee; padding-top: 20px;\">Se você não realizou esta solicitação, nenhuma ação é necessária e seu acesso continuará seguro.</p>" +
                                 "</div>";

            enviarEmail(user.getEmail(), "Recuperação de Senha - Legato", htmlMessage);
        }
        
        return ResponseFactory.ok("Se o e-mail existir, as instruções foram enviadas.", null);
    }

    // 1. Rota GET: Quando o usuário clica no link do e-mail, abre a tela do formulário
    @GetMapping(value = "/reset-password", produces = "text/html")
    public ResponseEntity<String> showResetPasswordForm(@RequestParam String token) {
        // Verifica se o token existe antes de mostrar a tela
        if (userRepository.findByPasswordResetToken(token).isEmpty()) {
            return ResponseEntity.badRequest().body("<h2 style=\"text-align: center; color: red; margin-top: 50px;\">Token inválido ou expirado.</h2>");
        }

        // Formulário HTML para digitar a nova senha
        String htmlForm = "<!DOCTYPE html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Legato - Nova Senha</title></head>" +
                "<body style=\"font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f9; margin: 0;\">" +
                "<div style=\"text-align: center; background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 100%; max-width: 400px;\">" +
                "<h2 style=\"color: #686AE7; margin-bottom: 20px;\">Crie sua nova senha</h2>" +
                "<form action=\"/auth/reset-password\" method=\"POST\">" +
                "<input type=\"hidden\" name=\"token\" value=\"" + token + "\">" + // Esconde o token no form
                "<input type=\"password\" name=\"newPassword\" placeholder=\"Digite sua nova senha\" required style=\"box-sizing: border-box; padding: 12px; width: 100%; border-radius: 5px; border: 1px solid #ccc; margin-bottom: 20px;\"><br>" +
                "<button type=\"submit\" style=\"background-color: #686AE7; color: white; padding: 12px; width: 100%; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px;\">Salvar Nova Senha</button>" +
                "</form>" +
                "</div></body></html>";

        return ResponseEntity.ok(htmlForm);
    }

    // 2. Rota POST: Quando o usuário clica no botão "Salvar Nova Senha" do HTML acima
    @PostMapping(value = "/reset-password", produces = "text/html")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token de recuperação inválido."));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);

        // Tela de sucesso HTML
        String htmlSuccess = "<!DOCTYPE html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Legato - Senha Alterada</title></head>" +
                "<body style=\"font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f9; margin: 0;\">" +
                "<div style=\"text-align: center; background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">" +
                "<h1 style=\"color: #686AE7; margin-bottom: 10px;\"> Senha Atualizada!</h1>" +
                "<p style=\"color: #333; font-size: 18px;\">Sua senha foi alterada com sucesso.</p>" +
                "<p style=\"color: #666; margin-top: 20px;\">Você já pode fechar esta página e fazer login no aplicativo com a sua nova senha.</p>" +
                "</div></body></html>";

        return ResponseEntity.ok(htmlSuccess);
    }

    // Método auxiliar para disparar e-mails de verdade depois (descomentar quando configurar o SMTP)
    private void enviarEmail(String para, String assunto, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // O 'true' ativa o multipart e a codificação UTF-8 garante acentos corretos
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("legatoapi@gmail.com");
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(htmlBody, true); // O "true" aqui diz que o texto é HTML!
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}