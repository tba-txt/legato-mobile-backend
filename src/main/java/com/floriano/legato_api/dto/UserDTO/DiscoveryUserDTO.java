package com.floriano.legato_api.dto.UserDTO;

import com.floriano.legato_api.model.User.AuxiliaryEntity.Location;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class DiscoveryUserDTO {
    private Long id;
    private String username;
    private String displayName;
    private String profilePicture;
    private List<String> photosCard;
    private String sex;
    private LocalDate birthDate;
    private List<String> instruments;
    private List<String> genres;
    private String bio;
    private Location location;
}