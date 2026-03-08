package com.floriano.legato_api.mapper.user;

import com.floriano.legato_api.dto.UserDTO.LocationUserDTO;
import com.floriano.legato_api.model.User.User;

public class LocationUserMapper {

    public static LocationUserDTO toDTO(User user, double distanceKm) {

        double rounded = round(distanceKm, 2);

        LocationUserDTO dto = new LocationUserDTO();
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
