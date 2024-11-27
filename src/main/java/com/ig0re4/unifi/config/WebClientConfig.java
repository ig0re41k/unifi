package com.ig0re4.unifi.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {

    @Value("${unifi.httpClient.timeout}")
    private int timeout;

    @Profile("proxy")
    @Bean
    @Lazy
    ProxyProvider proxy(@Value("${proxy.host}") String host,
                        @Value("${proxy.port}") int port) {
        return ProxyProvider.builder().type(ProxyProvider.Proxy.HTTP)
                .host(host).port(port).build();
    }

    @Profile("proxy")
    @Bean
    @Lazy
    HttpClient getHttpClientWithProxy(ProxyProvider proxy) throws SSLException {
        SslContext sslContext = getSslContext();
        return HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .tcpConfiguration(client ->
                        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                        .proxy(provider -> setProxy(proxy, provider)));
    }

    private void setProxy(ProxyProvider src, ProxyProvider.TypeSpec dst){
        dst.type(ProxyProvider.Proxy.HTTP).address(src.getAddress()).build();
    }

    @Profile("!proxy")
    @Bean
    @Lazy
    HttpClient getHttpClientWithoutProxy() throws SSLException {
        SslContext sslContext = getSslContext();
        return HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .tcpConfiguration(client ->
                        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout));
    }

    private SslContext getSslContext() throws SSLException {
        return SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
    }

    @Bean
    public WebClient webClient(HttpClient httpClient, ObjectMapper om) {
        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(
                            new Jackson2JsonEncoder(om, MediaType.APPLICATION_JSON));
                    configurer.defaultCodecs().jackson2JsonDecoder(
                            new Jackson2JsonDecoder(om, MediaType.APPLICATION_JSON));
                }).build();

        return WebClient.builder()
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
                })
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }


    @Bean
    public ObjectMapper getOm() {
        ObjectMapper om = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        om.registerModule(module);

        om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        om.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        om.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        return om;
    }
}