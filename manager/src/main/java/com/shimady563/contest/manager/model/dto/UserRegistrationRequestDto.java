package com.shimady563.contest.manager.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {
    @NotNull(message = "contest version id cannot be null")
    private Long contestVersionId;

    @NotNull(message = "contest id cannot be null")
    private Long contestId;
}
