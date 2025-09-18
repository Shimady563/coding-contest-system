package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.converter.CodeSubmissionConverter;
import com.shimady563.contest.manager.exception.SubmissionInvalidException;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.CodeSubmissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final UserService userService;
    private final ContestVersionService contestVersionService;
    private final TaskService taskService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.submission}")
    public String topic;

    public void submitSolution(CodeSubmissionDto submissionDto) {
        log.info("Submitting solution: {}", submissionDto);
        checkSubmissionValidity(submissionDto);
        kafkaTemplate.send(topic, CodeSubmissionConverter.request2Transport(submissionDto));
    }

    private void checkSubmissionValidity(CodeSubmissionDto submission) {
        log.info("Checking if the code submission is valid: {}", submission);
        ContestVersion contestVersion = contestVersionService.getContestVersionById(submission.getContestVersionId());
        Contest contest = contestVersion.getContest();

        if (submission.getSubmittedAt().isBefore(contest.getStartTime())
                || submission.getSubmittedAt().isAfter(contest.getEndTime())) {
            throw new SubmissionInvalidException("Submission was sent after or before the start of the contest: " + submission);
        }

        User user = userService.getUserById(submission.getUserId());
        Task task = taskService.getTaskByIdInternal(submission.getTaskId());

        if (!contestVersion.getUsers().contains(user)) {
            throw new SubmissionInvalidException("User with id: " + submission.getUserId() + " doesn't have the access to contest version with id: " + submission.getContestVersionId());
        } else if (!contestVersion.getTasks().contains(task)) {
            throw new SubmissionInvalidException("Task with id: " + submission.getTaskId() + " not found in contest version with id: " + submission.getContestVersionId());
        }
    }
}
