package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Task response model")
public class TaskResponseDto {
    @Schema(description = "Task id", example = "1")
    private Long id;

    @Schema(description = "Task name", example = "Quick Sort")
    private String name;

    @Schema(description = "Task description", example = "Implement quick sort algorithm for sorting an array of integers.")
    private String description;

    @Schema(description = "List of test cases")
    private List<TestCaseResponseDto> testCases;
}
