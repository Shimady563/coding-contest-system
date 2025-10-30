package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Task create/update request")
public class TaskRequestDto {
    @NotBlank(message = "name cannot be blank")
    @Schema(description = "Task name", example = "Quick Sort")
    private String name;

    @NotBlank(message = "description cannot be blank")
    @Schema(description = "Task description", example = "Implement quick sort algorithm for soring an array of integers.")
    private String description;

    @Valid
    @NotEmpty(message = "test cases cannot be empty")
    @Schema(description = "List of test cases")
    private List<TestCaseRequestDto> testCases;
}
