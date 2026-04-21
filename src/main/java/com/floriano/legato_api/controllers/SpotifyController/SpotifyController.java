package com.floriano.legato_api.controllers.SpotifyController;

import com.floriano.legato_api.dto.spotify.TopArtistsResponse;
import com.floriano.legato_api.payload.ApiResponse;
import com.floriano.legato_api.services.SpotifyService.SpotifyArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spotify")
@Tag(name = "Spotify")
public class SpotifyController {

    private final SpotifyArtistService spotifyArtistService;

    public SpotifyController(SpotifyArtistService spotifyArtistService) {
        this.spotifyArtistService = spotifyArtistService;
    }

    @Operation(summary = "Get user's top artists from Spotify", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/top-artists")
    public ResponseEntity<ApiResponse<TopArtistsResponse>> getTopArtists(@RequestHeader("X-Spotify-Token") String spotifyToken) {
        TopArtistsResponse topArtists = spotifyArtistService.getTopArtists(spotifyToken);
        return ResponseEntity.ok(new ApiResponse<>(true, "Artistas recuperados com sucesso do Spotify", topArtists));
    }
}
