package com.floriano.legato_api.model.User.enums;

public enum InstrumentList {
    GUITARRISTA("guitarrista"),
    TECLADISTA("tecladista"),
    BATERISTA("baterista"),
    VOCALISTA("vocalista"),
    COMPOSITOR("compositor"),
    BAIXISTA("baixista"),
    DJ("dj"),
    OUTRO("outro");


    private String instrument;

    InstrumentList(String role) {
        this.instrument = role;
    }

    public String getRole() {
        return instrument;
    }
}
