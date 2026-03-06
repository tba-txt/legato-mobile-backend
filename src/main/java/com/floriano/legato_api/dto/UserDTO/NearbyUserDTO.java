package com.floriano.legato_api.dto.UserDTO;

import com.floriano.legato_api.model.User.AuxiliaryEntity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NearbyUserDTO {
    private Long id;
    private String username;
    private String displayName;
    private String profilePicture;
    private Location location;
    private double distanceKm;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalLinksDTO {
        private String instagram;
        private String spotify;
        private String youtube;
        private String website;
    }
}