package com.shimady563.contest.manager.config;

import com.shimady563.contest.manager.config.props.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.shimady563.contest.manager.config.props.KafkaTopicProperties.TopicInfo;

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
}
