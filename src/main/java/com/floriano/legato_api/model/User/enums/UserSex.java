package com.floriano.legato_api.model.User.enums;

public enum UserSex {
    MALE("man"),
    WOMEN("women"),
    OTHER("other");

    private String sex;

    UserSex(String role) {
        this.sex = role;
    }

    public String getRole() {
        return sex;
    }
}
