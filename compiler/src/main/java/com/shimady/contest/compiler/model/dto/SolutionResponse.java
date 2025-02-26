package com.shimady.contest.compiler.model.dto;

import com.shimady.contest.compiler.model.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolutionResponse {
    private Long id;
    private Status status;
    private Long testsPassed;
}
