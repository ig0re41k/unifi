package com.ig0re4.unifi.model;

import lombok.Getter;

@Getter
public enum BasicRole {
    ROLE_ADMIN("ADMIN");
    private final String name;
    BasicRole(String name){
        this.name = name;
    }
}
