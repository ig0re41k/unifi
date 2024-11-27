package com.ig0re4.unifi.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Igor Shindel(212579997)
 * @since 24-03-2019
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public static ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("UNIFI API")
                .description("UNIFI API")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .version("1.0.0-SNAPSHOT")
                .contact(new Contact("ig0re4", "", "admin@ge.com"))
                .build();
    }

    @Bean
    public Docket customImplementation(ApiInfo apiInfo){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ig0re4.unifi"))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(Timestamp.class, Long.class)
                .securitySchemes(basicScheme());
    }

    private List<SecurityScheme> basicScheme() {
        return Lists.newArrayList(new BasicAuth("basicAuth"));
    }
}
