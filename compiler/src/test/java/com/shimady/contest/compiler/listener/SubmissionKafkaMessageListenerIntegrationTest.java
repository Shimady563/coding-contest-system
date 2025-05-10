package com.shimady.contest.compiler.listener;

import com.shimady.contest.compiler.config.KafkaTestConfig;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import com.shimady.contest.compiler.model.User;
import com.shimady.contest.compiler.model.dto.CodeSubmission;
import com.shimady.contest.compiler.repository.TaskRepository;
import com.shimady.contest.compiler.repository.UserRepository;
import com.shimady.contest.compiler.service.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.after;

@SpringBootTest
@Testcontainers
@Import(KafkaTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubmissionKafkaMessageListenerIntegrationTest {
    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:alpine")
            .withDatabaseName("contest")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    @ServiceConnection
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.1"));

    @Value("${kafka.topic.1}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @MockitoSpyBean
    private SubmissionService submissionService;

    @MockitoSpyBean
    private SubmissionKafkaMessageListener listener;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("Email");
        user.setPassword("Password");
        userRepository.save(user);

        var testCase = new TestCase();
        testCase.setInput("input");
        testCase.setOutput("output");
        var task = new Task();
        task.setName("name");
        task.setDescription("description");
        task.setTestCasesCount((short) 1);
        task.setTestCases(Set.of(testCase));
        testCase.setTask(task);
        taskRepository.save(task);
    }

    @Test
    public void testListenTechnologyMessage() {
        var partition = 0;
        var message = new CodeSubmission();
        message.setCode("code");
        message.setTaskId(1L);
        message.setUserId(1L);
        message.setSubmittedAt(LocalDateTime.now());

        kafkaTemplate.send(topic, partition, "", message);

        then(listener).should(after(5000)).listenSubmission(message, partition);
        then(submissionService).should().submitSolution(message);
    }
}
