package com.example.education.model.oauth;

public enum ERole {
    ROLE_USER("user"),
    ROLE_MODERATOR("pm"),
    ROLE_ADMIN("admin");

    private final String name;

    ERole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
