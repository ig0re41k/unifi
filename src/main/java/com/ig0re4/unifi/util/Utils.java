package com.ig0re4.unifi.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Slf4j
public class Utils {

    public static final String SUCCEED = "Succeed to {} : {}";
    public static final String FAIL = "Fail to ";
    public static final String UAA = "uaa";
    public static final String ADMIN = "ADMIN";

    private static final String BEARER = "Bearer ";
    private static final String BASIC = "Basic ";
    private static final Pattern hidePattern = Pattern.compile("(.{2})(.*)(.{2})");
    private static final String MASK = "*";
    private static final String REPLACE = "\0";
    private static final String PREDIX_ZONE_ID = "Predix-Zone-Id";
    private static final int MAX_ELEMENTS = 1000;

    public static String toString(final UUID entityValue)  {
        return Optional.ofNullable(entityValue)
                .map(UUID::toString)
                .orElse(null);
    }

    public static UUID fromString(final String databaseValue) {
        return Optional.ofNullable(databaseValue)
                .map(UUID::fromString)
                .orElse(null);
    }

    public static String[] toArray(List<String> list){
        return list.toArray(new String[0]);
    }


    public static String getPath(String path, String... pathSegments){
        return fromHttpUrl(path).pathSegment(pathSegments).build().toString();
    }

    public static Consumer<HttpHeaders> createHeaders(String username, String password){
        return headers -> headers.set(AUTHORIZATION, BASIC + encode(username, password));
    }

    private static String encode(String username, String password){
        String auth = username + ":" + password;
        return new String(encodeBase64(auth.getBytes(Charset.defaultCharset())));
    }

    private static String decode(String token){
        return new String(decodeBase64(token.getBytes(Charset.defaultCharset())));
    }


    public static String masking(String input) {
        return Optional.ofNullable(input)
                .map(i -> Optional.of(i)
                        .filter(l -> l.length() > 4)
                        .map(Utils::findMatch)
                        .orElse(i))
                .orElse(input);
    }

    private static String findMatch(String input){
        return Optional.of(hidePattern.matcher(input))
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1) +
                        replacement(matcher.group(2).length()) +
                        matcher.group(3))
                .orElse(input);
    }

    private static String replacement(int length) {
        return new String(new char[length]).replace(REPLACE, MASK);
    }

}
