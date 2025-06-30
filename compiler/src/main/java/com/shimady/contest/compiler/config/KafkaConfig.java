package com.shimady.contest.compiler.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                ((record, e) -> System.out.println("Discarding message due to: " + e.getCause().getMessage())),
                new FixedBackOff(5000L, 5)
        );
    }
}
