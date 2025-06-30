package com.shimady563.contest.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResponseDto {
    private Long id;
    private String input;
    private String output;
}
