package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.converter.CodeSubmissionConverter;
import com.shimady563.contest.manager.exception.SubmissionInvalidException;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.CodeSubmissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void submitSolution(CodeSubmissionDto submissionDto) {
        log.info("Submitting solution: {}", submissionDto);
        checkSubmissionValidity(submissionDto);
        kafkaTemplate.send("submissionTopic", CodeSubmissionConverter.request2Transport(submissionDto));
    }

    private void checkSubmissionValidity(CodeSubmissionDto submission) {
        log.info("Checking if the code submission is valid: {}", submission);
        if (submission.getSubmittedAt().isBefore(submission.getStartTime())
                || submission.getSubmittedAt().isAfter(submission.getEndTime())) {
            throw new SubmissionInvalidException("Submission was sent after or before the start of the contest: " + submission);
        }

        ContestVersion contestVersion = contestVersionService.getContestVersionById(submission.getContestVersionId());
        User user = userService.getUserById(submission.getUserId());
        Task task = taskService.getTaskById(submission.getTaskId());

        if (!contestVersion.getUsers().contains(user)) {
            throw new SubmissionInvalidException("User doesn't have the access to this task: " + submission);
        } else if (!contestVersion.getTasks().contains(task)) {
            throw new SubmissionInvalidException("Task with id " + submission.getTaskId() + " not found in contest version with id " + submission.getContestVersionId());
        }
    }
}
