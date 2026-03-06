package com.floriano.legato_api.dto.UserDTO;

import com.floriano.legato_api.model.User.AuxiliaryEntity.ExternalLinks;
import com.floriano.legato_api.model.User.AuxiliaryEntity.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String email;
    private String username;
    private String displayName;

    private String profilePicture;
    private String profileBanner;

    private List<String> photosCard = new ArrayList<>();

    private String sex;
    private List<String> instruments = new ArrayList<>();
    private List<String> favoriteArtistsSpotifyId = new ArrayList<>();
    private List<String> genres = new ArrayList<>();

    private String bio;
    private String goal;

    private Location location;
    private ExternalLinks links;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // SOCIAL
    private int followersCount;
    private int followingCount;
    private int connectionsCount;
    private int blockedCount;

    // CONNECTION REQUESTS
    private int sentRequestsCount;
    private int receivedRequestsCount;

    private Set<Long> connectionIds;
    private Set<Long> followerIds;
    private Set<Long> followingIds;
    private Set<Long> postsIds;
}
