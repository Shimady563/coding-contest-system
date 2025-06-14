package com.shimady563.contest.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmission {
    private String code;
    private Long taskId;
    private Long userId;
    private LocalDateTime submittedAt;
}
