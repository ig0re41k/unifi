package com.ig0re4.unifi.controller;

import com.ig0re4.unifi.model.*;
import com.ig0re4.unifi.service.UnifiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import javax.validation.Valid;


@Api(tags = "UNIFI API")
@RestController
@RequestMapping(value = "v2/unifi")
@Valid
public class UnifiController {

    @Autowired
    private UnifiService service;

    @GetMapping("health")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "health",
            authorizations = { @Authorization(value="basicAuth") })
    public Mono<String> health(){
        return service.health();
    }

    @GetMapping("ports")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "ports",
            authorizations = { @Authorization(value="basicAuth") })
    public Mono<String> ports(){
        return service.ports();
    }

    @GetMapping("clients")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "clients",
            authorizations = { @Authorization(value="basicAuth") })
    public Flux<UnifiResponse<UnifiNetworkClient>> clients(){
        return service.getClients();
    }

    @GetMapping("devices")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "devices",
            authorizations = { @Authorization(value="basicAuth") })
    public Flux<UnifiResponse<UnifiNetworkDevice>> devices(){
        return service.getDevices();
    }

    @PostMapping("reconnect/{name}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "reconnect",
            authorizations = { @Authorization(value="basicAuth") })
    public Flux<UnifiResponse<UnifyEmpty>> reconnect(@PathVariable String name){
        return service.reconnect(name);
    }

    @PostMapping("reconnect/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "reconnect all",
            authorizations = { @Authorization(value="basicAuth") })
    public Flux<UnifiResponse<UnifyEmpty>> reconnectAll(){
        return service.reconnectAll();
    }

    @GetMapping("vpn")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "vpn status",
            authorizations = { @Authorization(value="basicAuth") })
    public Flux<UnifiVpnRoute> getVpnStatus(){
        return service.getVpnStatus();
    }

    @PutMapping("vpn/{route}/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "set vpn on/off",
            authorizations = { @Authorization(value="basicAuth") })
    public Flux<UnifiVpnRoute> setVpnStatus(Routes route, VpnStatus status){
        return service.setVpnStatus(route, status);
    }
}
