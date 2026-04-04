package com.floriano.legato_api.model.User.enums;

public enum InstrumentList {
    GUITARRA("guitarra"),
    VIOLAO("violão"),
    BAIXO("baixo"),
    BATERIA("bateria"),
    TECLADO("teclado"),
    PIANO("piano"),
    VIOLINO("violino"),
    SAXOFONE("saxofone"),
    FLAUTA("flauta"),
    TROMPETE("trompete"),
    VOZ_CANTO("voz/canto"),
    PRODUCAO_MUSICAL("produção musical"),
    DJ("dj"),
    COMPOSICAO("composição"),
    ARRANJO("arranjo"),
    GRAVACAO("gravação"),
    MIXAGEM("mixagem"),
    MASTERIZACAO("masterização"),
    CAVAQUINHO("cavaquinho"),
    UKULELE("ukulele"),
    MANDOLIN("mandolin"),
    CELLO("cello"),
    OUTRO("outro");

    private String instrument;

    InstrumentList(String role) {
        this.instrument = role;
    }

    public String getRole() {
        return instrument;
    }
}