package com.floriano.legato_api.model.User.AuxiliaryEntity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ExternalLinks {
    private String spotify;
    private String soundcloud;
    private String instagram;
    private String youtube;
    private String website;
}
