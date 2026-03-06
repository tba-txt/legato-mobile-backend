package com.floriano.legato_api.mapper.user;

import com.floriano.legato_api.dto.UserDTO.UserListDTO;
import com.floriano.legato_api.dto.UserDTO.UserResponseDTO;
import com.floriano.legato_api.model.User.User;

import java.util.stream.Collectors;

public class UserMapper {



    public static UserResponseDTO toDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setProfileBanner(user.getProfileBanner());
        dto.setPhotosCard(user.getPhotosCard());
        dto.setSex(user.getSex() != null ? user.getSex().name() : null);
        dto.setInstruments(user.getInstruments()
                .stream().map(Enum::name).collect(Collectors.toList()));
        dto.setFavoriteArtistsSpotifyId(
                user.getFavoriteArtists().stream()
                        .map(a -> a.getSpotifyId())
                        .collect(Collectors.toList())
        );
        dto.setGenres(user.getGenres()
                .stream().map(Enum::name).collect(Collectors.toList()));
        dto.setBio(user.getBio());
        dto.setGoal(user.getGoal());
        dto.setLocation(user.getLocation());
        dto.setLinks(user.getLinks());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        dto.setFollowersCount(user.getFollowers().size());
        dto.setFollowingCount(user.getFollowing().size());
        dto.setConnectionsCount(user.getConnections().size());
        dto.setBlockedCount(user.getBlockedUsers().size());

        dto.setSentRequestsCount(user.getSentRequests().size());
        dto.setReceivedRequestsCount(user.getReceivedRequests().size());

        dto.setConnectionIds(
                user.getConnections().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet())
        );
        dto.setFollowerIds(
                user.getFollowers().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet())
        );
        dto.setFollowingIds(
                user.getFollowing().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet())
        );

        return dto;
    }

    public static UserListDTO toListDTO(User user) {
        UserListDTO dto = new UserListDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setProfilePicture(user.getProfilePicture());

        dto.setFollowersCount(user.getFollowers().size());
        dto.setFollowingCount(user.getFollowing().size());

        return dto;
    }
}
