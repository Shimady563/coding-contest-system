package com.shimady563.contest.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionResponseDto {
    private Long id;
    private String status;
    private LocalDateTime submittedAt;
    // below are for specific request
    private String taskName;
    private String username;
    private String code;
}
