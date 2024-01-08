package com.example.demo.models;

public enum UserEnum {

    ADMIN("admin"),

    USER("user");

    private String role;

    UserEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}
