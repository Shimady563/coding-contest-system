package com.shimady563.contest.manager.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration for contest request")
public class UserRegistrationRequestDto {
    @NotNull(message = "contest version id cannot be null")
    @Schema(description = "Contest version id", example = "7")
    private Long contestVersionId;

    @NotNull(message = "contest id cannot be null")
    @Schema(description = "Contest id", example = "3")
    private Long contestId;
}
