package com.ig0re4.unifi.model;

import lombok.Getter;

@Getter
public enum Routes {
    Livingroom("AppleTV Livingroom"),
    Bedroom("AppleTV Master bedroom"),
    iMac("Igor's IMac");

    private final String name;

    Routes(String name){
        this.name = name;
    }
}
