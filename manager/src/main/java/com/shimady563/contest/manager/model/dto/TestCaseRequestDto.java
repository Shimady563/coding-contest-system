package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Single test case for a task")
public class TestCaseRequestDto {

    @NotBlank(message = "input cannot be blank")
    @Schema(description = "Test input", example = "4\n1 3 5 7")
    private String input;

    @NotBlank(message = "output cannot be blank")
    @Schema(description = "Expected output", example = "8")
    private String output;
}
