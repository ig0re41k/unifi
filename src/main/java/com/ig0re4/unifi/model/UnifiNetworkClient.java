package com.ig0re4.unifi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnifiNetworkClient {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("network_id")
    private String networkId;
    private String name;
    private String ip;
    private String mac;
    private String network;
}
