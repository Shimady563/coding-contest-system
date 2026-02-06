package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.model.dto.ContestRequestDto;
import com.shimady563.contest.manager.model.dto.ContestResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ContestService {
    ContestResponseDto getContestById(Long id);

    Page<ContestResponseDto> getContestsByName(String name, PageRequest pageRequest);

    ContestResponseDto createContest(ContestRequestDto request);

    void updateContestById(Long id, ContestRequestDto request);

    List<ContestResponseDto> getContestsByGroupId(Long groupId);

    void deleteContestById(Long id);
}
