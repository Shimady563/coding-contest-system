package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.dto.ContestRequestDto;
import com.shimady563.contest.manager.model.dto.ContestResponseDto;
import com.shimady563.contest.manager.service.ContestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contests")
@RequiredArgsConstructor
public class ContestController {
    private final ContestService contestService;

    @GetMapping("")
    @Secured({"ROLE_TEACHER"})
    public Page<ContestResponseDto> getContestsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return contestService.getContestsByName(name, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({"ROLE_TEACHER"})
    public void createContest(@Valid @RequestBody ContestRequestDto request) {
        contestService.createContest(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    public void updateContest(@PathVariable Long id, @Valid @RequestBody ContestRequestDto request) {
        contestService.updateContestById(id, request);
    }

    @GetMapping("/group")
    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    public List<ContestResponseDto> getContestsByGroupId(@RequestParam Long groupId) {
        return contestService.getContestsByGroupId(groupId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    public void deleteContestById(@PathVariable Long id) {
        contestService.deleteContestById(id);
    }
}
