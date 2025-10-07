package com.shimady563.contest.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Test case response model")
public class TestCaseResponseDto {
    @Schema(description = "Test case id", example = "5")
    private Long id;

    @Schema(description = "Input", example = "4\n1 3 5 7")
    private String input;

    @Schema(description = "Expected output", example = "8")
    private String output;
}
