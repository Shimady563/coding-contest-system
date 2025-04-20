package com.shimady563.contest.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestVersionResponseDto {
    private Long id;
    private String name;
    private List<Long> taskIds;
}
