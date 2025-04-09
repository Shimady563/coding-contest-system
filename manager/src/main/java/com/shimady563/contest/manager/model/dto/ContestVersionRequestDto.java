package com.shimady563.contest.manager.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestVersionRequestDto {
    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotEmpty(message = "tasks cannot be empty")
    private List<TaskRequestDto> tasks;
}
