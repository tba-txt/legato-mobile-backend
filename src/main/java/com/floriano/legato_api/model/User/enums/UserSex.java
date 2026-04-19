package com.floriano.legato_api.model.User.enums;

public enum UserSex {
    MALE("man"),
    FEMALE("women"),
    OTHER("other"),
    PREFER_NOT_TO_SAY("Prefiro não dizer");

    private String sex;

    UserSex(String role) {
        this.sex = role;
    }

    public String getRole() {
        return sex;
    }
}
