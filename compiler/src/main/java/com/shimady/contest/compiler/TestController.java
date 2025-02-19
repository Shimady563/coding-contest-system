package com.shimady.contest.compiler;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.dto.CodeSubmission;
import com.shimady.contest.compiler.repository.TaskRepository;
import com.shimady.contest.compiler.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final SubmissionService submissionService;

    @PostMapping("/submit")
    public void run(@RequestBody CodeSubmission submission) {
        submissionService.submitSolution(submission);
    }

    private final TaskRepository taskRepository;
    @GetMapping("/test")
    public void test() {
        Task task = new Task();
        taskRepository.save(task);
    }
}
/*
        String code = """
                #include <iostream>
                int main() {
                    int x;
                    int y;
                    std::cin >> x;
                    std::cin >> y;
                    std::cout << (x + y) << std::endl;
                    return 0;
                }
                """;
#include <iostream> \n int main() { \n  int x; \n int y; \n std::cin >> x; \n std::cin >> y; \n std::cout << (x + y) << std::endl; \n return 0; \n}
 */