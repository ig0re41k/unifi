package com.ig0re4.unifi.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Retryable {

    @Value("${vpn.retry.duration}")
    private int duration;

    @Value("${vpn.retry.retries}")
    private int retries;

    protected Retry retry(String... command) {
        String commands = Optional.ofNullable(command)
                            .map(s -> String.join(" ", command))
                            .orElse("unknown");
        return Retry.backoff(retries, Duration.ofMillis(duration))
                .jitter(0.5d)
                .doAfterRetry(retrySignal -> log.info("Retried {} : {} times",
                        commands, retrySignal.totalRetries()))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal)
                        -> new TimeoutException("for " + commands + " retry"));
    }
}
