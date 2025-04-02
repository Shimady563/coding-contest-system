package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Status;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilerService {
    private static final String DIR_PATH = System.getProperty("user.home") + "/cpp";

    private final SolutionService solutionService;
    private final TestCaseService testCaseService;

    public void compileAndRun(String code, Task task) {
        log.info("Compiling and running code for task with id: {}", task.getId());
        String fileId = String.valueOf(UUID.randomUUID());
        Path filePath = Path.of(DIR_PATH, fileId + ".cpp").toAbsolutePath();
        Path executablePath = Path.of(DIR_PATH, fileId).toAbsolutePath();

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, code, StandardOpenOption.CREATE);

            CompletableFuture<Process> compileResult = compile(filePath, executablePath);
            compileResult.thenAccept(process -> {
                try {
                    if (process.exitValue() != 0) {
                        String result = new String(process.getInputStream().readAllBytes());
                        log.info("Compilation failed: {}", result);
                        solutionService.createSolution(code, Status.COMPILE_ERROR, (short) 0, task);
                    }
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            });
            compileResult.get();

            List<TestCase> testCases = testCaseService.getAllTestCasesByTask(task);
            Short testsPassed = 0;

            for (TestCase testCase : testCases) {
                Process runnerProcess = startRunnerProcess(executablePath);
                Short localTestsPassed = testsPassed;
                CompletableFuture<String> resultFuture = runnerProcess.onExit()
                        .completeOnTimeout(null, 10, TimeUnit.SECONDS)
                        .thenApply(process -> {
                            if (process == null) {
                                log.info("Runner process timed out on test: {}", localTestsPassed + 1);
                                solutionService.createSolution(code, Status.TIMED_OUT, localTestsPassed, task);
                                return null;
                            }
                            try {
                                String result = new String(process.getInputStream().readAllBytes());
                                if (process.exitValue() != 0) {
                                    log.info("Runner process exited with code: {}, on test: {}, result: {}", process.exitValue(), localTestsPassed + 1, result);
                                    solutionService.createSolution(code, Status.RUNTIME_ERROR, localTestsPassed, task);
                                    return null;
                                }
                                return result;
                            } catch (IOException e) {
                                throw new CompletionException(e);
                            }
                        });

                writeInput(runnerProcess, testCase.getInput());
                String result = resultFuture.get();

                if (result == null) {
                    solutionService.createSolution(code, Status.INTERNAL_ERROR, (short) 0, task);
                    return;
                }

                if (!result.strip().equals(testCase.getOutput())) {
                    solutionService.createSolution(code, Status.WRONG_ANSWER, testsPassed, task);
                    return;
                }
                testsPassed++;
            }

            solutionService.createSolution(code, Status.ACCEPTED, testsPassed, task);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while working with processes: {}", e.getMessage());
            solutionService.createSolution(code, Status.INTERNAL_ERROR, (short) 0, task);
        } catch (IOException e) {
            log.error("Error while working with files: {}", e.toString());
            solutionService.createSolution(code, Status.INTERNAL_ERROR, (short) 0, task);
        }
    }

    private static CompletableFuture<Process> compile(Path filePath, Path executablePath) throws IOException {
        log.info("Compiling code from file: {}", filePath);
        return new ProcessBuilder("g++", filePath.toString(), "-o", executablePath.toString())
                .redirectErrorStream(true)
                .start()
                .onExit();
    }

    private static Process startRunnerProcess(Path executablePath) throws IOException {
        log.info("Starting process: {}", executablePath);
        return new ProcessBuilder(executablePath.toString())
                .redirectErrorStream(true)
                .start();
    }

    private static void writeInput(Process p, String input) throws IOException {
        log.info("Writing input to process: {}", input);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()))) {
            writer.write(input);
        }
    }
}
