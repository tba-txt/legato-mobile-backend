package com.floriano.legato_api.services.SpotifyService;

import com.floriano.legato_api.dto.spotify.TopArtistsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class SpotifyArtistService {

    private final SpotifyAuthService spotifyAuthService;
    private final RestTemplate restTemplate = new RestTemplate();

    public SpotifyArtistService(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    public TopArtistsResponse getTopArtists(String userOAuthToken) {
        String url = "https://api.spotify.com/v1/me/top/artists";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + userOAuthToken);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<TopArtistsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, TopArtistsResponse.class);

        return response.getBody();
    }
}
