package com.shimady.contest.compiler.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${kafka.topic.submission}")
    private String submissionTopic;

    @Bean
    public NewTopic submissionTopic() {
        return TopicBuilder
                .name(submissionTopic)
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                ((record, e) -> System.out.println("Discarding message due to: " + e.getCause().getMessage())),
                new FixedBackOff(5000L, 5)
        );
    }
}
