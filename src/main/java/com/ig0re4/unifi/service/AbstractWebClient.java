package com.ig0re4.unifi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.ig0re4.unifi.util.Constants.X_CSRF_TOKEN;
import static io.netty.handler.codec.http.HttpScheme.HTTPS;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public abstract class AbstractWebClient {

    @Value("${unifi.site.host}")
    private String host;

    @Value("${unifi.site.name}")
    private String site;

    @Autowired
    private WebClient webClient;

    abstract void convert(MultiValueMap<String, ResponseCookie> src, MultiValueMap<String, String> dest);

    <T> Mono<T> getMono(String endpoint,
                        MultiValueMap<String, ResponseCookie> cookies,
                        Function<ClientResponse, Mono<T>> handleResponse){
        return webClient.get()
                .uri(builder -> builder.scheme(HTTPS.toString())
                        .host(host)
                        .path(replace(endpoint))
                        .build())
                .cookies(c -> convert(cookies, c))
                .exchange()
                .flatMap(response -> Mono.just(response)
                    .filter(r -> r.statusCode().is2xxSuccessful())
                    .flatMap(handleResponse)
                    .switchIfEmpty(Mono.defer(this::handleException)));
    }

    <T> Flux<T> getFlux(String endpoint,
                        MultiValueMap<String, ResponseCookie> cookies,
                        Function<ClientResponse, Flux<T>> handleResponse){
        return webClient.get()
                .uri(builder -> builder.scheme(HTTPS.toString())
                        .host(host)
                        .path(replace(endpoint))
                        .build())
                .cookies(c -> convert(cookies, c))
                .exchange()
                .flatMapMany(response -> Mono.just(response)
                        .filter(r -> r.statusCode().is2xxSuccessful())
                        .flatMapMany(handleResponse)
                        .switchIfEmpty(Mono.defer(this::handleException)));
    }

    <T, Z> Mono<T> postMono(String endpoint,
                            Z body,
                            MultiValueMap<String, ResponseCookie> cookies,
                            String xsrfToken,
                            Function<ClientResponse, Mono<T>> handleResponse){
        return webClient.post()
                .uri(builder -> builder.scheme(HTTPS.toString())
                        .host(host)
                        .path(replace(endpoint))
                        .build())
                .contentType(APPLICATION_JSON)
                .header(X_CSRF_TOKEN, xsrfToken)
                .bodyValue(body)
                .cookies(c -> convert(cookies, c))
                .exchange()
                .flatMap(response -> Mono.just(response)
                        .filter(r -> r.statusCode().is2xxSuccessful())
                        .flatMap(handleResponse)
                        .switchIfEmpty(Mono.defer(this::handleException)));
    }

    <T, Z> Flux<T> postFlux(String endpoint,
                            Z body,
                            MultiValueMap<String, ResponseCookie> cookies,
                            String xsrfToken,
                            Function<ClientResponse, Flux<T>> handleResponse){
        return webClient.post()
                .uri(builder -> builder.scheme(HTTPS.toString())
                        .host(host)
                        .path(replace(endpoint))
                        .build())
                .contentType(APPLICATION_JSON)
                .header(X_CSRF_TOKEN, xsrfToken)
                .bodyValue(body)
                .cookies(c -> convert(cookies, c))
                .exchange()
                .flatMapMany(response -> Mono.just(response)
                        .filter(r -> r.statusCode().is2xxSuccessful())
                        .flatMapMany(handleResponse)
                        .switchIfEmpty(Mono.defer(this::handleException)));
    }

    <T, Z> Mono<T> putMono(String endpoint,
                           Z body,
                           MultiValueMap<String, ResponseCookie> cookies,
                           String xsrfToken,
                           Function<ClientResponse, Mono<T>> handleResponse){
        return webClient.put()
                .uri(builder -> builder.scheme(HTTPS.toString())
                        .host(host)
                        .path(replace(endpoint))
                        .build())
                .contentType(APPLICATION_JSON)
                .header(X_CSRF_TOKEN, xsrfToken)
                .bodyValue(body)
                .cookies(c -> convert(cookies, c))
                .exchange()
                .flatMap(response -> Mono.just(response)
                        .filter(r -> r.statusCode().is2xxSuccessful())
                        .flatMap(handleResponse)
                        .switchIfEmpty(Mono.defer(this::handleException)));
    }

    <T, Z> Flux<T> putFlux(String endpoint,
                           Z body,
                           MultiValueMap<String, ResponseCookie> cookies,
                           String xsrfToken,
                           Function<ClientResponse, Flux<T>> handleResponse){
        return webClient.put()
                .uri(builder -> builder.scheme(HTTPS.toString())
                        .host(host)
                        .path(replace(endpoint))
                        .build())
                .contentType(APPLICATION_JSON)
                .header(X_CSRF_TOKEN, xsrfToken)
                .bodyValue(body)
                .cookies(c -> convert(cookies, c))
                .exchange()
                .flatMapMany(response -> Mono.just(response)
                        .filter(r -> r.statusCode().is2xxSuccessful())
                        .flatMapMany(handleResponse)
                        .switchIfEmpty(Mono.defer(this::handleException)));
    }

    private String replace(String endpoint){
        return endpoint.replaceAll("#site#", site);
    }

    <T> Mono<T> handleException(){
        return Mono.error(() -> new RuntimeException("exception occurred"));
    }
}
