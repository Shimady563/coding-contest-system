package com.shimady.contest.compiler;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.User;
import com.shimady.contest.compiler.model.dto.CodeSubmission;
import com.shimady.contest.compiler.model.dto.SolutionResponse;
import com.shimady.contest.compiler.repository.TaskRepository;
import com.shimady.contest.compiler.repository.UserRepository;
import com.shimady.contest.compiler.service.SolutionService;
import com.shimady.contest.compiler.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final SubmissionService submissionService;
    private final SolutionService solutionService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @PostMapping("/submit")
    public void run(@RequestBody CodeSubmission submission) {
        if (!taskRepository.existsById(1L)) {
            Task task = new Task();
            task.setName("");
            task.setDescription("");
            task.setTestCasesCount((short) 0);
            taskRepository.save(task);
        }
        if (!userRepository.existsById(1L)) {
            User user = new User();
            user.setEmail("");
            user.setPassword("");
            userRepository.save(user);
        }
        submissionService.submitSolution(submission);
    }

    @GetMapping("/solutions")
    public List<SolutionResponse> getSolutionsByTaskId(@RequestParam Long taskId) {
        return solutionService.getSolutionsByTaskId(taskId);
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