package com.shimady563.contest.manager.model.dto;

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
public class TaskRequestDto {
    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "description cannot be blank")
    private String description;

    @NotNull(message = "contest version id cannot be null")
    private Long contestVersionId;

    @NotEmpty(message = "test cases cannot be empty")
    private List<TestCaseDto> testCases;
}
