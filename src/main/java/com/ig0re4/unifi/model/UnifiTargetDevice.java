package com.ig0re4.unifi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnifiTargetDevice {
    @JsonProperty("client_mac")
    private String clientMac;
    private String type;
}