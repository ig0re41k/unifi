package com.ig0re4.unifi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@NoArgsConstructor
@ConfigurationProperties(prefix = "unifi.site.credentials")
public class UnifiCredentials {
    private String username = "";
    private String password = "";

    private String jsonProperty(String key, String value){
        return addQuotes(key) + ":" + addQuotes(value);
    }

    private String addQuotes(String string){
        return "\"" + string + "\"";
    }

    public String toJson(){
        return "{" + jsonProperty("username", username) + "," +
                jsonProperty("password", password) + "}";
    }
}
