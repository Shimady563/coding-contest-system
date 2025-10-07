package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.service.TestCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test-cases")
@RequiredArgsConstructor
@Tag(name = "Test Cases", description = "Endpoints for managing test cases")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized (no/invalid token)",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)))
})
public class TestCaseController {
    private final TestCaseService testCaseService;

    @DeleteMapping("")
    @Secured("ROLE_TEACHER")
    @Operation(summary = "Delete test cases by their IDs",
            description = "Allows deletion of multiple test cases by providing a list of their IDs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test cases deleted successfully")
    })
    public void deleteTestCasesByIds(@RequestBody List<Long> testCaseIds) {
        testCaseService.deleteByIds(testCaseIds);
    }
}
