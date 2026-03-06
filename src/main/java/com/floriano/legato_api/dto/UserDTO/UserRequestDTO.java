package com.floriano.legato_api.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    // CAMPOS BÁSICOS
    private String email;
    private String username;
    private String displayName;
    private String profilePicture;
    private String profileBanner;
    private List<String> photosCard;
    private String sex;
    private String bio;
    private String goal;

    // PREFERÊNCIAS MUSICAIS
    private List<String> instruments;
    private List<String> genres;

    // LOCALIZAÇÃO
    private LocationDTO location;

    // LINKS EXTERNOS
    private ExternalLinksDTO links;

    // CLASSES AUXILIARES

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private String city;
        private String state;
        private String country;
    }

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
