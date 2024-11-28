package com.ig0re4.unifi.service;

import com.ig0re4.unifi.model.UnifiCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static com.ig0re4.unifi.util.Constants.*;

public class CookiesManager
        extends AbstractWebClient {

    private static final MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();
    private LocalDateTime lastTimeUpdated;

    @Autowired
    private UnifiCredentials unifiCredentials;

    Mono<MultiValueMap<String, ResponseCookie>> getCookies(){
        return Mono.just(cookies)
                .filter(c -> isEmptyOrExpired())
                .switchIfEmpty(Mono.defer(this::updateCookies));
    }

    @Override
    void convert(MultiValueMap<String, ResponseCookie> src,
                 MultiValueMap<String, String> dest){
        src.forEach((key, value) ->
                dest.put(key, value.stream()
                        .map(HttpCookie::getValue).collect(Collectors.toList())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private Mono<MultiValueMap<String, ResponseCookie>> updateCookies(){
        return postMono(LOGIN_ENDPOINT,
                   unifiCredentials.toJson(),
                   cookies,
                   "",
                   response -> {
                lastTimeUpdated = LocalDateTime.now();
                cookies.clear();
                cookies.putAll(response.cookies());
                return Mono.just(cookies);
        });
    }

    private boolean isEmptyOrExpired(){
        return !cookies.isEmpty() && !isAnyCookieExpired();

    }

    private boolean isAnyCookieExpired(){
        return cookies.values().stream().flatMap(cl ->
                        cl.stream().map(c ->
                                lastTimeUpdated.plus(c.getMaxAge()).isBefore(LocalDateTime.now())))
                .findAny().orElse(false);
    }
}
