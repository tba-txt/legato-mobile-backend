package com.floriano.legato_api.services.SpotifyService;

import com.floriano.legato_api.dto.spotify.SpotifyTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyAuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SpotifyAuthService spotifyAuthService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(spotifyAuthService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(spotifyAuthService, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(spotifyAuthService, "restTemplate", restTemplate);
    }

    @Test
    void getAccessToken_Success() {
        SpotifyTokenResponse tokenResponse = new SpotifyTokenResponse("test-access-token", "Bearer", 3600);
        String url = "https://accounts.spotify.com/api/token";

        when(restTemplate.postForObject(eq(url), any(), eq(SpotifyTokenResponse.class))).thenReturn(tokenResponse);

        String accessToken = spotifyAuthService.getAccessToken();

        assertEquals("test-access-token", accessToken);
    }

    @Test
    void getAccessToken_Failure() {
        String url = "https://accounts.spotify.com/api/token";
        when(restTemplate.postForObject(eq(url), any(), eq(SpotifyTokenResponse.class))).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spotifyAuthService.getAccessToken();
        });

        assertEquals("Não foi possível obter o token de acesso do Spotify", exception.getMessage());
    }
}
