package com.shimady563.contest.manager.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CodeSubmission {
    private String code;
    private Long taskId;
    private Long userId;
    private LocalDateTime submittedAt;
}
