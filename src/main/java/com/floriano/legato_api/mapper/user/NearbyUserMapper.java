package com.floriano.legato_api.mapper.user;

import com.floriano.legato_api.dto.UserDTO.NearbyUserDTO;
import com.floriano.legato_api.model.User.User;

public class NearbyUserMapper {

    public static NearbyUserDTO toDTO(User user, double distanceKm) {

        double rounded = round(distanceKm, 2);

        NearbyUserDTO dto = new NearbyUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setLocation(user.getLocation());
        dto.setDistanceKm(rounded);

        return dto;
    }

    private static double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
}
