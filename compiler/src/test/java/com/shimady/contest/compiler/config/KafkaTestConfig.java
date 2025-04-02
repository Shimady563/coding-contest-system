package com.shimady.contest.compiler.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.List;

@TestConfiguration
public class KafkaTestConfig {

    @Bean
    public NewTopic skillTopic() {
        return TopicBuilder.name("solutionTopic")
                .partitions(4)
                .replicas(1)
                .build();
    }

//    @Bean
//    public Consumer<String, Object> consumer(ConsumerFactory<?, ?> factory) {
//        var consumer = factory.createConsumer();
//        consumer.subscribe(List.of("solutionTopic"));
//        return (Consumer<String, Object>) consumer;
//    }
}
