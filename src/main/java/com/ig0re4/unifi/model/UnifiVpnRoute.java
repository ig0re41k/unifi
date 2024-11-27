package com.ig0re4.unifi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Setter
@Getter
@Accessors(chain = true)
public class UnifiVpnRoute {

    private String description;

    @JsonProperty("network_id")
    private String networkId;

    @JsonProperty("_id")
    private String id;

    private boolean enabled;

    @JsonProperty("target_devices")
    private List<UnifiTargetDevice> targetDevices;
    
    private  List<String> domains;
    
    private  String interfaceName;
    
    @JsonProperty("ip_addresses")
    private  List<String> ipAddresses;
    
    @JsonProperty("ip_ranges")
    private  List<String> ipRanges;
    
    private  List<String> regions;
    
    private  boolean isAllTab;
    
    private  boolean isLocalNetwork;
    
    @JsonProperty("kill_switch_enabled")
    private  boolean killSwitchEnabled;

    @JsonProperty("matching_target")
    private  String matchingTarget;

}
