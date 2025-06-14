package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Solution;
import com.shimady563.contest.manager.model.Status;
import com.shimady563.contest.manager.model.dto.SolutionResponseDto;
import com.shimady563.contest.manager.repository.SolutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {

    @Mock
    private SolutionRepository solutionRepository;

    @InjectMocks
    private SolutionService solutionService;

    @Test
    void shouldReturnMappedSolutionById() {
        Long id = 1L;
        Solution solution = new Solution();
        SolutionResponseDto dto = new SolutionResponseDto();
        dto.setSubmittedAt(solution.getSubmittedAt());

        given(solutionRepository.findById(id)).willReturn(Optional.of(solution));

        SolutionResponseDto result = solutionService.getSolutionById(id);

        assertEquals(dto, result);
    }

    @Test
    void shouldThrowWhenSolutionNotFoundById() {
        given(solutionRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solutionService.getSolutionById(999L));
    }

    @Test
    void shouldSearchForSolutionsWithFilters() {
        Solution solution = new Solution();
        SolutionResponseDto dto = new SolutionResponseDto();
        dto.setSubmittedAt(solution.getSubmittedAt());
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Solution> solutionPage = new PageImpl<>(List.of(solution));

        given(solutionRepository.findAll(any(Specification.class), eq(pageRequest))).willReturn(solutionPage);

        Page<SolutionResponseDto> result = solutionService.searchForSolutions(
                Status.ACCEPTED,
                1L,
                2L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                pageRequest
        );

        assertThat(result.getContent()).hasSize(1).containsExactly(dto);
    }
}
