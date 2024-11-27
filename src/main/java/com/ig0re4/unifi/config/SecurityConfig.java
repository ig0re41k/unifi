package com.ig0re4.unifi.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurerComposite;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.ig0re4.unifi.model.BasicRole.ROLE_ADMIN;
import static com.ig0re4.unifi.util.Utils.toArray;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig{

    @Value("${unifi.security.admin.username}")
    private String adminUsername;

    @Value("${unifi.security.admin.password}")
    private String adminPassword;

    @Getter
    @Configuration
    @ConfigurationProperties(prefix = "unifi.security.access")
    public static class AccessList {
        private final List<String> whitelist = Lists.newArrayList();
        private final List<String> api = Lists.newArrayList();
    }

    @Getter
    @Autowired
    private AccessList acl;

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    SecurityWebFilterChain whiteList(ServerHttpSecurity http) {
         return build(init(http)
                 .securityMatcher(whiteList())
                    .authorizeExchange()
                        .matchers(whiteList()).permitAll());
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    SecurityWebFilterChain api(ServerHttpSecurity http) {
        return build(init(http)
                .securityMatcher(api())
                    .httpBasic()
                .and()
                    .authorizeExchange()
                        .matchers(api()).authenticated());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MapReactiveUserDetailsService userDetailsService() {
        return new MapReactiveUserDetailsService(
                User.withUsername(adminUsername)
                    .password(adminPassword)
                    .roles(ROLE_ADMIN.getName())
                    .build());
    }

    @SuppressWarnings("ALL")
    @Bean
    WebFluxConfigurer corsConfigurer() {
        return new WebFluxConfigurerComposite() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowCredentials(true)
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowedMethods("*");
            }
        };
    }

    @Bean
    ServerAuthenticationEntryPoint unauthorizedEntryPoint(){
        return (exchange, e) -> Mono.fromRunnable(() -> exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED));
    }

    private ServerHttpSecurity init(ServerHttpSecurity http) {
        return http
                    .csrf().disable()
                    .formLogin().disable()
                    .logout().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedEntryPoint())
                .and();
    }

    private SecurityWebFilterChain build(ServerHttpSecurity.AuthorizeExchangeSpec spec){
        return spec
                .and()
                    .authorizeExchange().
                        anyExchange()
                            .authenticated()
                .and()
                .build();
    }

    private ServerWebExchangeMatcher matches(List<String> list) {
        return matches(toArray(list));
    }

    private ServerWebExchangeMatcher matches(String... routes) {
        return ServerWebExchangeMatchers.pathMatchers(routes);
    }

    private ServerWebExchangeMatcher whiteList() {
        return matches(acl.getWhitelist());
    }

    private ServerWebExchangeMatcher api() {
        return matches(acl.getApi());
    }
}
