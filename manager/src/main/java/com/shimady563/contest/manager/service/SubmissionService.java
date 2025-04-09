package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.model.dto.CodeSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void submitSolution(CodeSubmission submission) {
        log.info("Submitting solution: {}", submission);
        kafkaTemplate.send("submissionTopic", submission);
    }
}
