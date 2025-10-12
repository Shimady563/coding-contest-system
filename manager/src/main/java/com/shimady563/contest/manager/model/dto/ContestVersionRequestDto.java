package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Contest version create request")
public class ContestVersionRequestDto {
    @NotBlank(message = "name cannot be blank")
    @Schema(description = "Version name", example = "Group A - attempt 1")
    private String name;

    @NotNull(message = "contest id cannot be null")
    @Schema(description = "Contest id", example = "42")
    private Long contestId;

    @NotEmpty(message = "task ids cannot be empty")
    @Schema(description = "List of task ids included in this version", example = "[1, 2, 3]")
    private List<Long> taskIds;
}
