package com.ig0re4.unifi.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Builder
@Slf4j
public class UnifiRequest {
    private String mac;
    private String cmd;

    public static UnifiRequest just(String mac, String cmd){
        return UnifiRequest.builder()
                .mac(mac)
                .cmd(cmd)
                .build();
    }
}
