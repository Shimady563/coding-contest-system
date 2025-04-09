package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.service.ContestVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contest-versions")
@RequiredArgsConstructor
public class ContestVersionController {
    private final ContestVersionService contestVersionService;

}
