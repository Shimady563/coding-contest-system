package com.shimady563.contest.manager.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestRequestDto {
    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "description cannot be blank")
    private String description;

    @NotNull(message = "start time cannot be null")
    private LocalDateTime startTime;

    @NotNull(message = "end time cannot be null")
    private LocalDateTime endTime;

    @NotNull(message = "group id cannot be null")
    private Long groupId;

    @NotEmpty(message = "contest versions cannot be empty")
    private List<ContestVersionRequestDto> contestVersions;
}
