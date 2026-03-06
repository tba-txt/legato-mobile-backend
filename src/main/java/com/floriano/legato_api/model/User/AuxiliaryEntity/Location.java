package com.floriano.legato_api.model.User.AuxiliaryEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class Location {
    private double latitude;
    private double longitude;
    private String city;
    private String state;
    private String country;
    @Column(name = "location_updated_at")
    private LocalDateTime updatedAt;
}
