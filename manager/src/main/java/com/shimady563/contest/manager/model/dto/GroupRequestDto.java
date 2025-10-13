package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Group create/update request")
public class GroupRequestDto {
    @NotBlank(message = "name cannot be blank")
    @Schema(description = "Group name", example = "15/1")
    private String name;
}
