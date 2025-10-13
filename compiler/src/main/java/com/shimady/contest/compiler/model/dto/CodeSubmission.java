package com.shimady.contest.compiler.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CodeSubmission {

    @NotBlank(message = "code cannot be blank")
    private String code;

    @NotNull(message = "task id cannot be null")
    private Long taskId;

    @NotNull(message = "user id cannot be null")
    private Long userId;

    @NotNull(message = "submission date cannot be null")
    private LocalDateTime submittedAt;
}
