package com.shimady563.contest.manager.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Contest create/update request")
public class ContestRequestDto {
    @NotBlank(message = "name cannot be blank")
    @Schema(description = "Contest name", example = "Algorithms Basics")
    private String name;

    @NotBlank(message = "description cannot be blank")
    @Schema(description = "Contest description", example = "Arrays, sorting and complexity")
    private String description;

    @NotNull(message = "start time cannot be null")
    @Schema(description = "Start time (ISO-8601)", example = "2025-01-20T09:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "end time cannot be null")
    @Schema(description = "End time (ISO-8601)", example = "2025-01-20T11:00:00")
    private LocalDateTime endTime;

    @NotNull(message = "group id cannot be null")
    @Schema(description = "Target group id", example = "1")
    private Long groupId;
}
