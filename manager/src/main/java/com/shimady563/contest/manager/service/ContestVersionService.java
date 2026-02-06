package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.model.dto.ContestVersionRequestDto;
import com.shimady563.contest.manager.model.dto.ContestVersionResponseDto;

import java.util.List;

public interface ContestVersionService {
    void createContestVersion(ContestVersionRequestDto request);

    List<ContestVersionResponseDto> getContestVersionsByContestId(Long contestId);

    void deleteContestVersionById(Long id);
}
