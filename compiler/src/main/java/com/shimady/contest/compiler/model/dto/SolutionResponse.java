package com.shimady.contest.compiler.model.dto;

import com.shimady.contest.compiler.model.SolutionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolutionResponse {
    private Long id;
    private SolutionStatus status;
    private Long testsPassed;
}
