package com.ig0re4.unifi.service;

import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.ig0re4.unifi.util.Constants.*;

public class XsrfTokenManager
    extends CookiesManager{

    Mono<String> getXsrfToken(MultiValueMap<String, ResponseCookie> cookies){
        return getMono(SELF_ENDPOINT, cookies, this::getXsrfToken);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private Mono<String> getXsrfToken(ClientResponse response){
        return Mono.from(Flux.fromStream(response.headers().header(X_CSRF_TOKEN).stream()))
                .switchIfEmpty(Mono.defer(this::handleException));
    }
}
