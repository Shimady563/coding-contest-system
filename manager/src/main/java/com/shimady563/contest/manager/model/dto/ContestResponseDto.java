package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestResponseDto {
    @Schema(description = "Contest id", example = "3")
    private Long id;

    @Schema(description = "Contest name", example = "Algorithms Basics")
    private String name;

    @Schema(description = "Contest description", example = "Arrays, sorting and complexity")
    private String description;

    @Schema(description = "Start time (ISO-8601)", example = "2025-01-20T09:00:00")
    private LocalDateTime startTime;

    @Schema(description = "End time (ISO-8601)", example = "2025-01-20T11:00:00")
    private LocalDateTime endTime;

    @Schema(description = "Target group id", example = "1")
    private Long groupId;
}

