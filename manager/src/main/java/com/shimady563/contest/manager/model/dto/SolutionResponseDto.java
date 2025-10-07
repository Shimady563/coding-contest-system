package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionResponseDto {
    @Schema(description = "Solution id", example = "10")
    private Long id;

    @Schema(description = "Solution status", example = "Accepted")
    private String status;

    @Schema(description = "Submission time (ISO-8601)", example = "2025-01-20T09:30:00")
    private LocalDateTime submittedAt;

    // following fields are for individual solution request
    @Schema(description = "Target task name", example = "Quick Sort")
    private String taskName;

    @Schema(description = "Username of the submitter", example = "John Doe")
    private String username;

    @Schema(description = "Submitted code", example = "int main() { return 0; }")
    private String code;
}
