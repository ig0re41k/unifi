package com.ig0re4.unifi.service;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UnifiWebClient {
    <T> Mono<T> getMono(ParameterizedTypeReference<T> reference, String endpoint);
    <T> Flux<T> getFlux(ParameterizedTypeReference<T> reference, String endpoint);
    <T, Z> Mono<T> postMono(ParameterizedTypeReference<T> reference, String endpoint, Z body);
    <T, Z> Mono<T> putMono(ParameterizedTypeReference<T> reference, String endpoint, Z body);
}
