package com.ig0re4.unifi.service;


import com.ig0re4.unifi.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.ig0re4.unifi.model.VpnStatus.on;
import static com.ig0re4.unifi.util.Constants.*;

@Slf4j
@Service
public class UnifiServiceImpl
        implements UnifiService {

    @Autowired
    private UnifiWebClient unifiClient;

    @Override
    public Mono<String> health() {
        return unifiClient.getMono(new ParameterizedTypeReference<>() {}, HEALTH_ENDPOINT);
    }

    @Override
    public Mono<String> ports() {
        return unifiClient.getMono(new ParameterizedTypeReference<>() {}, PORT_FORWARD_ENDPOINT);
    }

    @Override
    public Flux<UnifiResponse<UnifiNetworkClient>> getClients() {
        return unifiClient.getFlux(new ParameterizedTypeReference<>() {}, CLIENTS_ENDPOINT);
    }

    @Override
    public Flux<UnifiResponse<UnifiNetworkDevice>> getDevices() {
        return unifiClient.getFlux(new ParameterizedTypeReference<>() {}, DEVICES_ENDPOINT);
    }

    @Override
    public Flux<UnifiResponse<UnifyEmpty>> reconnect(String name) {
        return getClients()
            .flatMap(response ->
                Flux.fromStream(response.getData().stream())
                    .filter(client -> !StringUtils.isEmpty(client.getName()) &&
                                name.equals(client.getName()))
                    .flatMap(this::reconnect));
    }

    @Override
    public Flux<UnifiResponse<UnifyEmpty>> reconnectAll() {
        return getClients()
                .flatMap(response -> Flux.fromStream(response.getData().stream())
                    .flatMap(this::reconnect));
    }

    @Override
    public Flux<UnifiVpnRoute> getVpnStatus() {
        return unifiClient.getFlux(new ParameterizedTypeReference<>() {}, TRAFFIC_ROUTES_ENDPOINT);
    }

    @Override
    public Flux<UnifiVpnRoute> setVpnStatus(String route, VpnStatus status) {
        return getVpnStatus()
                .filter(uvr -> !StringUtils.isEmpty(uvr.getDescription()) &&
                        route.equals(uvr.getDescription()))
                .flatMap(uvr -> setVpnStatus(uvr, status));
    }

    private Mono<UnifiResponse<UnifyEmpty>> reconnect(UnifiNetworkClient client){
        return unifiClient.postMono(new ParameterizedTypeReference<>() {}, RECONNECT_ENDPOINT,
                UnifiRequest.just(client.getMac(), RECONNECT_CMD));
    }

    private Mono<UnifiVpnRoute> setVpnStatus(UnifiVpnRoute route, VpnStatus status){
        return unifiClient.putMono(new ParameterizedTypeReference<>() {},
                TRAFFIC_ROUTES_ENDPOINT + "/" + route.getId(),
                route.setEnabled(status.equals(on)));
    }
}
