package com.shimady563.contest.manager.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmissionDto {
    @NotEmpty(message = "code cannot be blank")
    private String code;

    @NotNull(message = "task id cannot be null")
    private Long taskId;

    @NotNull(message = "user id cannot be null")
    private Long userId;

    @NotNull(message = "contest version id cannot be null")
    private Long contestVersionId;

    @NotNull(message = "start time cannot be null")
    private LocalDateTime startTime;

    @NotNull(message = "end time cannot be null")
    private LocalDateTime endTime;

    @NotNull(message = "submitted at cannot be null")
    private LocalDateTime submittedAt;
}
