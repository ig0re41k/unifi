package com.ig0re4.unifi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 *
 * @author Igor Shindel(212579997)
 * @since 25-10-2016
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

    static final String WEBJARS = "/webjars/**";
    static final String WEBJARS_LOCATION = "classpath:/META-INF/resources/webjars/";
    static final String RESOURCES_PATTERN = "/resources/**";
    static final String RESOURCES_HANDLER = "src/main/webapp/resources/**";
    static final String RESOURCES_LOCATION = "classpath:/META-INF/resources/webjars/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        if (!registry.hasMappingForPattern(WEBJARS)) {
            registry.addResourceHandler(WEBJARS)
                    .addResourceLocations(WEBJARS_LOCATION);
        }
        if (!registry.hasMappingForPattern(RESOURCES_PATTERN)) {
            registry.addResourceHandler(RESOURCES_HANDLER)
                    .addResourceLocations(RESOURCES_LOCATION);
        }
    }
}
