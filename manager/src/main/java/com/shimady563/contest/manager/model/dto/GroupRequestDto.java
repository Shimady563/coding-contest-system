package com.shimady563.contest.manager.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Group create/update request")
public class GroupRequestDto {
    @NotEmpty(message = "name cannot be empty")
    @Schema(description = "Group name", example = "15/1")
    private String name;
}
