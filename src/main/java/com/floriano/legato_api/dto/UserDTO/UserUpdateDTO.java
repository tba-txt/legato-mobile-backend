package com.floriano.legato_api.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String profilePicture;
    private String profileBanner;
    private List<String> photosCard;
    private String sex;
    private String bio;
    private String goal;

    private List<String> instruments;
    private List<String> genres;

    private LocationDTO location;
    private ExternalLinksDTO links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private Double latitude;
        private Double longitude;
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
