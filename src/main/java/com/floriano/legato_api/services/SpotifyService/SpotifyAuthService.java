package com.floriano.legato_api.services.SpotifyService;

import com.floriano.legato_api.dto.spotify.SpotifyTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";

    // NOVO: Troca o Código recebido do Mobile por Tokens reais
    public SpotifyTokenResponse exchangeCodeForTokens(String code, String redirectUri) {
        HttpHeaders headers = createHeaders();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri); // Deve ser igual ao cadastrado no Dashboard do Spotify

        return postToSpotify(body, headers);
    }

    // NOVO: Usa o refresh_token para pegar um novo access_token sem deslogar o usuário
    public SpotifyTokenResponse refreshToken(String refreshToken) {
        HttpHeaders headers = createHeaders();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        return postToSpotify(body, headers);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String auth = clientId + ":" + clientSecret;
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
        return headers;
    }

    private SpotifyTokenResponse postToSpotify(MultiValueMap<String, String> body, HttpHeaders headers) {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<SpotifyTokenResponse> response = restTemplate.postForEntity(SPOTIFY_TOKEN_URL, entity, SpotifyTokenResponse.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Erro ao comunicar com Spotify API. Status: " + response.getStatusCode());
    }
}