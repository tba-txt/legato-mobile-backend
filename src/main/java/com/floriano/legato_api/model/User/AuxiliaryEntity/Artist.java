package com.floriano.legato_api.model.User.AuxiliaryEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Artist {
    @Id
    private String spotifyId;
    private String name;
}
