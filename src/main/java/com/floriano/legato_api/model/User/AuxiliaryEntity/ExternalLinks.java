package com.floriano.legato_api.model.User.AuxiliaryEntity;

import org.hibernate.annotations.ColumnTransformer;

import com.floriano.legato_api.infra.security.SecurityConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ExternalLinks {


    @Column(columnDefinition = "bytea")
        @ColumnTransformer(
        read = "pgp_sym_decrypt(spotify::bytea, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')",
        write = "pgp_sym_encrypt(CAST(? AS text), '" + SecurityConstants.DB_ENCRYPTION_KEY + "')"
    )
    private String spotify;

    @Column(columnDefinition = "bytea")
        @ColumnTransformer(
            read = "pgp_sym_decrypt(soundcloud::bytea, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')",
            write = "pgp_sym_encrypt(CAST(? AS text), '" + SecurityConstants.DB_ENCRYPTION_KEY + "')"
        )
    private String soundcloud;

    @Column(columnDefinition = "bytea")
        @ColumnTransformer(
        read = "pgp_sym_decrypt(instagram::bytea, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')",
        write = "pgp_sym_encrypt(CAST(? AS text), '" + SecurityConstants.DB_ENCRYPTION_KEY + "')"
    )
    private String instagram;

    @Column(columnDefinition = "bytea")
        @ColumnTransformer(
            read = "pgp_sym_decrypt(youtube::bytea, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')",
            write = "pgp_sym_encrypt(CAST(? AS text), '" + SecurityConstants.DB_ENCRYPTION_KEY + "')"
        )
    private String youtube;

    @Column(columnDefinition = "bytea")
        @ColumnTransformer(
            read = "pgp_sym_decrypt(website::bytea, '" + SecurityConstants.DB_ENCRYPTION_KEY + "')",
            write = "pgp_sym_encrypt(CAST(? AS text), '" + SecurityConstants.DB_ENCRYPTION_KEY + "')"
        )
    private String website;
}
