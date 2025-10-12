package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contest version response model")
public class ContestVersionResponseDto {
    @Schema(description = "Version id", example = "7")
    private Long id;

    @Schema(description = "Version name", example = "Group A - attempt 1")
    private String name;

    @Schema(description = "Ids of tasks included in the version", example = "[1, 2, 3]")
    private List<Long> taskIds;
}
