package com.ig0re4.unifi.service;

import com.ig0re4.unifi.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UnifiService {
    Flux<UnifiResponse<UnifiNetworkClient>> getClients();
    Flux<UnifiResponse<UnifiNetworkDevice>> getDevices();
    Flux<UnifiResponse<UnifyEmpty>> reconnect(String name);
    Flux<UnifiResponse<UnifyEmpty>> reconnectAll();
    Flux<UnifiVpnRoute> getVpnStatus();
    Flux<UnifiVpnRoute> setVpnStatus(Routes route, VpnStatus status);
    Mono<String> health();
    Mono<String> ports();
}
