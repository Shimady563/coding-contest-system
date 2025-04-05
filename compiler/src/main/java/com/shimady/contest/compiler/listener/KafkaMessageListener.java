package com.shimady.contest.compiler.listener;

import com.shimady.contest.compiler.model.dto.CodeSubmission;
import com.shimady.contest.compiler.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageListener {
    private final SubmissionService submissionService;

    @KafkaListener(topics = "${kafka.topic.1}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenSubmission(
            @Payload CodeSubmission submission,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition
    ) {
        log.info("Received submission message: {}, from partition: {}",
                submission,
                partition
        );
        submissionService.submitSolution(submission);
    }
}
