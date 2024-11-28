package com.ig0re4.unifi.service;


import com.google.common.collect.Lists;
import com.ig0re4.unifi.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ig0re4.unifi.model.VpnStatus.on;
import static com.ig0re4.unifi.util.Constants.*;

@Slf4j
@Service
public class UnifiServiceImpl
        implements UnifiService {

    @Getter
    @Setter
    @Configuration
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "unifi.site.reconnect")
    private static class ExcludedMacAddresses {
        List<String> exclude = Lists.newArrayList();
    }

    @Autowired
    private UnifiWebClient unifiClient;

    @Autowired
    private ExcludedMacAddresses excluded;

    private List<String> macAddresses;

    @PostConstruct
    public void init(){
        macAddresses = Lists.newArrayList(getMyMacAddresses());
        macAddresses.addAll(excluded.getExclude());
    }

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
            .filter(this::isNotMyClient)
            .flatMap(this::reconnect));
    }

    private boolean isNotMyClient(UnifiNetworkClient client) {
        return macAddresses.stream().noneMatch(mac -> mac.equalsIgnoreCase(client.getMac()));
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

    @SneakyThrows
    private List<String> getMyMacAddresses() {
        return NetworkInterface.networkInterfaces()
                .filter(nic -> null != getHardwareAddress(nic))
                .map(nic -> {
            ByteBuffer buffer = ByteBuffer.wrap(getHardwareAddress(nic));
            return IntStream.generate(buffer::get)
                    .limit(buffer.capacity())
                    .mapToObj(b -> String.format("%02X",(byte)b))
                    .collect(Collectors.joining(":"));
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    private byte[] getHardwareAddress(NetworkInterface nic){
        return nic.getHardwareAddress();
    }

}
