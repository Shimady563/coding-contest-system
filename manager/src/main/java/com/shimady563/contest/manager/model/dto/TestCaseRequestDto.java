package com.shimady563.contest.manager.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRequestDto {

    @NotBlank(message = "input cannot be blank")
    private String input;

    @NotBlank(message = "output cannot be blank")
    private String output;
}
