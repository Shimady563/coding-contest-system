package com.shimady.contest.compiler.config;

import com.shimady.contest.compiler.config.props.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import static com.shimady.contest.compiler.config.props.KafkaTopicProperties.TopicInfo;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaTopicProperties kafkaProperties;

    @Bean
    public NewTopic submissionTopic() {
        TopicInfo submission = kafkaProperties.getSubmission();
        return TopicBuilder
                .name(submission.getName())
                .partitions(submission.getPartitions())
                .replicas(submission.getReplicationFactor())
                .build();
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                ((record, e) -> log.error("Discarding message due to: {}", e.getCause().getMessage())),
                new FixedBackOff(5000L, 5)
        );
    }
}
