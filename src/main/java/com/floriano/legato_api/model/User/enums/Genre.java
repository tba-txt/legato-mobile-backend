package com.floriano.legato_api.model.User.enums;

public enum Genre {
    ROCK("Rock"),
    RAP("Rap"),
    MPB("MPB"),
    SERTANEJO("Sertanejo"),
    FUNK("Funk"),
    JAZZ("Jazz"),
    BLUES("Blues"),
    RB_SOUL("R&B & Soul"),
    HIP_HOP("Hip Hop"),
    ELETRONICO("Eletrônico"),
    HOUSE("House"),
    POP("Pop"),
    CLASSICO("Clássico"),
    METAL("Metal"),
    PUNK("Punk"),
    REGGAE("Reggae"),
    FORRO("Forró"),
    SAMBA("Samba"),
    BOSSA_NOVA("Bossa Nova"),
    GOSPEL("Gospel"),
    INDIE("Indie"),
    LOFI("Lo-Fi"),
    TRAP("Trap"),
    AXE("Axé"),
    OUTRO("Outro");

    private final String label;

    Genre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}