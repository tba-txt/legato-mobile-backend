package com.floriano.legato_api.model.User.enums;

public enum Genre {

    ROCK("Rock"),
    INDIE("Indie"),
    MPB("Mpb"),
    POP("Pop"),
    HIP_HOP("Hip Hop"),
    RNB_SOUL("R&B & Soul"),
    ELETRONICO("Eletrônico"),
    JAZZ("Jazz"),
    BLUES("Blues"),
    CLASSICO("Clássico"),
    FUNK("Funk"),
    METAL("Metal"),
    SERTANEJO("Sertanejo"),
    PAGODE("Pagode"),
    FORRO("Forró"),

    ROCK_PROGRESSIVO("Rock progressivo"),
    KPOP("K-pop"),
    LATINA("Latina"),
    LOFI("Lo-Fi"),
    REGGAE("Reggae"),
    GOSPEL("gospel"),
    AFRO("Afro"),
    HOUSE("House"),
    EDM("Dance & EDM"),
    TRAP("Trap"),
    PUNK("Punk"),
    FOLK("Folk"),
    DANCEHALL("Dancehall"),
    OUTRO("Outro");

    private final String label;

    Genre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
