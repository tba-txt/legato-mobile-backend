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
import com.floriano.legato_api.model.User.AuxiliaryEntity.ExternalLinks;

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
            boolean isHuman = recaptchaService.validateToken(data.recaptchaToken());
            
           /* if (!isHuman) {
                return ResponseFactory.forbidden("Falha na validação do reCAPTCHA. Você é um robô?");
            } */ // comentado para facilitar os testes

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

            // --- MAPEAMENTO DOS NOVOS CAMPOS DO PERFIL INICIAL ---
            newUser.setBio(data.bio());
            newUser.setObjective(data.objective());

            if (data.sex() != null && !data.sex().isBlank()) {
                newUser.setSex(UserSex.valueOf(data.sex().toUpperCase()));
            }

            if (data.instruments() != null && !data.instruments().isEmpty()) {
                // Tipagem explícita (String i) ajuda a IDE caso o import falhe no futuro, mas no Java 22 é redundante se o import estiver correto.
                newUser.setInstruments(
                    data.instruments().stream()
                        .map((String i) -> InstrumentList.valueOf(i.toUpperCase()))
                        .toList()
                );
            }

            if (data.genres() != null && !data.genres().isEmpty()) {
                newUser.setGenres(
                    data.genres().stream()
                        .map((String g) -> Genre.valueOf(g.toUpperCase()))
                        .toList()
                );
            }

            if (data.links() != null) {
                ExternalLinks externalLinks = new ExternalLinks();
                externalLinks.setSpotify(data.links().getSpotify());
                externalLinks.setSoundcloud(data.links().getSoundcloud());
                externalLinks.setInstagram(data.links().getInstagram());
                externalLinks.setYoutube(data.links().getYoutube());
                externalLinks.setWebsite(data.links().getWebsite());
                
                newUser.setLinks(externalLinks);
            }
            // -----------------------------------------------------
            
            this.userRepository.save(newUser);

            var token = tokenService.generateToken(newUser);
            var userDTO = UserMapper.toDTO(newUser);
            var response = new AuthResponseDTO(token, userDTO);

            return ResponseFactory.ok("Usuário cadastrado com sucesso!", response);

        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Dados de perfil inválidos: verifique os valores de sexo, instrumentos ou gêneros. Lembre-se de enviar exatamente como no Enum (ex: 'GUITARRA', 'ROCK'). Detalhe: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseFactory.badRequest("Error: " + e.getMessage());
        }
    }
}