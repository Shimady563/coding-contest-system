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
public class ContestVersionRequestDto {
    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "contest id cannot be null")
    private Long contestId;

    @NotEmpty(message = "task ids cannot be empty")
    private List<Long> taskIds;
}
