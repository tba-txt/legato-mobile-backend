package com.floriano.legato_api.services.SpotifyService;

import com.floriano.legato_api.dto.spotify.SpotifyArtist;
import com.floriano.legato_api.dto.spotify.TopArtistsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SpotifyAuthService spotifyAuthService;

    @InjectMocks
    private SpotifyArtistService spotifyArtistService;

    @Test
    void getTopArtists_Success() {
        String userOAuthToken = "user-oauth-token";
        String url = "https://api.spotify.com/v1/me/top/artists";

        SpotifyArtist artist = new SpotifyArtist("Artist Name", Collections.singletonList("Genre"), new SpotifyArtist.Images[]{new SpotifyArtist.Images("url")});
        TopArtistsResponse expectedResponse = new TopArtistsResponse(Collections.singletonList(artist));
        ResponseEntity<TopArtistsResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(TopArtistsResponse.class)))
                .thenReturn(responseEntity);

        TopArtistsResponse actualResponse = spotifyArtistService.getTopArtists(userOAuthToken);

        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.getItems().size());
        assertEquals("Artist Name", actualResponse.getItems().get(0).getName());
    }

    @Test
    void getTopArtists_Failure() {
        String userOAuthToken = "user-oauth-token";
        String url = "https://api.spotify.com/v1/me/top/artists";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(TopArtistsResponse.class)))
                .thenThrow(new RuntimeException("API call failed"));

        assertThrows(RuntimeException.class, () -> {
            spotifyArtistService.getTopArtists(userOAuthToken);
        });
    }
}
