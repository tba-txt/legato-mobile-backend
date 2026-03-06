package com.floriano.legato_api.dto.UserDTO;

import lombok.Data;

@Data
public class UserListDTO {
    private Long id;
    private String username;
    private String displayName;
    private String profilePicture;

    private int followersCount;
    private int followingCount;
}

