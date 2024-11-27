package com.ig0re4.unifi.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnifiNetworkDevice {
    private String name;
    private String model;
    private String mac;
}
