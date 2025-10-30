package com.shimady563.contest.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Group response model")
public class GroupResponseDto {
    @Schema(description = "Group id", example = "1")
    private Long id;

    @Schema(description = "Group name", example = "15/1")
    private String name;
}
