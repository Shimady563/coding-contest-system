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
public class SubmissionDto {
    @NotEmpty(message = "code cannot be blank")
    private String code;

    @NotNull(message = "task id cannot be null")
    private Long taskId;

    @NotNull(message = "user id cannot be null")
    private Long userId;

    @NotNull(message = "end time cannot be null")
    private LocalDateTime endTime;

    private LocalDateTime submittedAt;
}
