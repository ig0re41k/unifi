package com.ig0re4.unifi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UnifiWebClientImpl
        extends XsrfTokenManager
        implements UnifiWebClient {

    @Override
    public <T> Mono<T> getMono(ParameterizedTypeReference<T> reference, String endpoint){
        return getCookies()
                .flatMap(cookies -> getMono(endpoint, cookies,
                        response -> response.bodyToMono(reference)));
    }

    @Override
    public <T> Flux<T> getFlux(ParameterizedTypeReference<T> reference, String endpoint){
        return getCookies()
            .flatMapMany(cookies -> getFlux(endpoint, cookies,
            response -> response.bodyToFlux(reference)));
    }

    @Override
    public <T, Z> Mono<T> postMono(ParameterizedTypeReference<T> reference, String endpoint, Z body) {
        return getCookies()
            .flatMap(cookie -> getXsrfToken(cookie)
                .flatMap(xsrfToken -> postMono(endpoint, body, cookie, xsrfToken,
            response -> response.bodyToMono(reference))));
    }

    @Override
    public <T, Z> Mono<T> putMono(ParameterizedTypeReference<T> reference, String endpoint, Z body) {
        return getCookies()
            .flatMap(cookie -> getXsrfToken(cookie)
                .flatMap(xsrfToken -> putMono(endpoint, body, cookie, xsrfToken,
                response -> response.bodyToMono(reference))));
    }
}
