package com.floriano.legato_api.model.User.enums;

public enum InstrumentList {
    GUITARRA("guitarra"),
    TECLADO("teclado"),
    BATERIA("bateria"),
    VOCAL("vocal"),
    COMPOSITOR("compositor"),
    BAIXO("baixo"),
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
