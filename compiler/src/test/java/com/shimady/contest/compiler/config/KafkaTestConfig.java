package com.shimady.contest.compiler.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaTestConfig {
    @Value("${kafka.topic.1}")
    private String topic;

    @Bean
    public NewTopic submissionTopic() {
        return TopicBuilder.name(topic)
                .partitions(4)
                .replicas(1)
                .build();
    }
}
