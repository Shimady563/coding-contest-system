package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Status;
import com.shimady.contest.compiler.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilerService {
    private final SolutionService solutionService;
    private static final String DIR_PATH = System.getProperty("user.home") + "/cpp";
    // test cases for testing purposes
    private final static Map<String, String> testCases = Map.of(
            "1 2", "3",
            "5 6", "11",
            "156 123", "279"
    );

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
                        solutionService.createSolution(code, Status.COMPILE_ERROR, 0L, task);
                    }
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            });
            compileResult.get();

            Long testsPassed = 0L;

            for (String input : testCases.keySet()) {
                Process runnerProcess = startRunnerProcess(executablePath);
                Long localTestsPassed = testsPassed;
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

                writeInput(runnerProcess, input);
                String result = resultFuture.get();

                if (result == null) {
                    return;
                }

                if (!result.strip().equals(testCases.get(input))) {
                    solutionService.createSolution(code, Status.WRONG_ANSWER, testsPassed, task);
                }
                testsPassed++;
            }

            solutionService.createSolution(code, Status.ACCEPTED, testsPassed, task);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while working with processes: {}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error while working with files: {}", e.toString());
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
