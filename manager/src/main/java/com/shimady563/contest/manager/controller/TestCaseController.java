package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test-cases")
@RequiredArgsConstructor
public class TestCaseController {
    private final TestCaseService testCaseService;

    @DeleteMapping("")
    @Secured("ROLE_TEACHER")
    public void deleteByIds(@RequestBody List<Long> testCaseIds) {
        testCaseService.deleteByIds(testCaseIds);
    }
}
