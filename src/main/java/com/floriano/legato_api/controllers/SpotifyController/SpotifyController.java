package com.floriano.legato_api.controllers.SpotifyController;

import com.floriano.legato_api.dto.spotify.SpotifyTokenResponse;
import com.floriano.legato_api.dto.spotify.TopArtistsResponse;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.payload.ApiResponse;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.SpotifyService.SpotifyArtistService;
import com.floriano.legato_api.services.SpotifyService.SpotifyAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spotify")
@Tag(name = "Spotify")
public class SpotifyController {

    private final SpotifyArtistService spotifyArtistService;
    private final SpotifyAuthService spotifyAuthService;
    private final UserRepository userRepository;

    public SpotifyController(SpotifyArtistService spotifyArtistService, 
                             SpotifyAuthService spotifyAuthService,
                             UserRepository userRepository) {
        this.spotifyArtistService = spotifyArtistService;
        this.spotifyAuthService = spotifyAuthService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Vincular conta do Spotify usando código de autorização", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/callback")
    public ResponseEntity<ApiResponse<Void>> handleCallback(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String code, 
            @RequestParam String redirectUri) {
            
        // 1. Troca o código pelo token real lá na API do Spotify
        SpotifyTokenResponse tokens = spotifyAuthService.exchangeCodeForTokens(code, redirectUri);
        
        // 2. Salva na conta do usuário
        User user = userRepository.findById(userPrincipal.getUser().getId())
                                  .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                                  
        user.setSpotifyAccessToken(tokens.getAccessToken());
        // O refresh token às vezes não vem em requisições subsequentes, então só atualiza se vier
        if (tokens.getRefreshToken() != null) {
            user.setSpotifyRefreshToken(tokens.getRefreshToken());
        }
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<>(true, "Conta do Spotify vinculada com sucesso!", null));
    }

    @Operation(summary = "Puxar os top artistas do usuário logado", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/top-artists")
    public ResponseEntity<ApiResponse<TopArtistsResponse>> getTopArtists(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        User user = userRepository.findById(userPrincipal.getUser().getId())
                                  .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (user.getSpotifyAccessToken() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Usuário não vinculou o Spotify.", null));
        }

        try {
            // Tenta buscar os artistas com o token atual
            TopArtistsResponse topArtists = spotifyArtistService.getTopArtists(user.getSpotifyAccessToken());
            return ResponseEntity.ok(new ApiResponse<>(true, "Artistas recuperados com sucesso!", topArtists));
            
        } catch (Exception e) {
            // Se der erro (geralmente porque o token expirou após 1 hora), tentamos usar o Refresh Token
            if (user.getSpotifyRefreshToken() != null) {
                SpotifyTokenResponse novosTokens = spotifyAuthService.refreshToken(user.getSpotifyRefreshToken());
                user.setSpotifyAccessToken(novosTokens.getAccessToken());
                userRepository.save(user);
                
                // Tenta de novo com o token novo
                TopArtistsResponse topArtists = spotifyArtistService.getTopArtists(novosTokens.getAccessToken());
                return ResponseEntity.ok(new ApiResponse<>(true, "Artistas recuperados (Token atualizado)!", topArtists));
            }
            throw new RuntimeException("Falha na autenticação com o Spotify. O usuário precisa fazer login novamente.");
        }
    }
}