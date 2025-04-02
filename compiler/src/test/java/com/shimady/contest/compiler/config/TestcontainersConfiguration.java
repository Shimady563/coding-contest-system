package com.shimady.contest.compiler.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
    KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.1"));
	}

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"))
                .withDatabaseName("contest")
                .withUsername("postgres")
                .withPassword("postgres");
    }

}
