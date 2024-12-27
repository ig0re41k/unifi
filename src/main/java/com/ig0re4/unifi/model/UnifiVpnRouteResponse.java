package com.ig0re4.unifi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

@Setter
@Getter
@Accessors(chain = true)
@Builder
public class UnifiVpnRouteResponse {
    private String name;
    private boolean state;
    @JsonIgnore
    private UnifiVpnRouteRequest request;

    public static Mono<UnifiVpnRouteResponse> just(UnifiVpnRouteRequest request){
        return Mono.just(UnifiVpnRouteResponse.builder()
                .name(request.getDescription())
                .request(request)
                .state(request.isEnabled())
                .build());
    }
}
