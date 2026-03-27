package com.floriano.legato_api.model.User.AuxiliaryEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.floriano.legato_api.infra.security.SecurityConstants;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnTransformer;

@Embeddable
@Getter
@Setter
public class Location {
    private double latitude;
    private double longitude;

    @Column(columnDefinition = "bytea")
        @ColumnTransformer(
            read = "pgp_sym_decrypt(city::bytea, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')",
            write = "pgp_sym_encrypt(?, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')"
        )
    private String city;

    @Column(columnDefinition = "bytea")
        @ColumnTransformer(
            read = "pgp_sym_decrypt(state::bytea, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')",
            write = "pgp_sym_encrypt(?, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')"
        )
    private String state;

    @Column(columnDefinition = "bytea")
        @ColumnTransformer(
            read = "pgp_sym_decrypt(country::bytea, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')",
            write = "pgp_sym_encrypt(?, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')"
        )
    private String country;

    @Column(name = "location_updated_at")
    private LocalDateTime updatedAt;
}
