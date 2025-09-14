package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.SolutionStatus;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import com.shimady.contest.compiler.model.dto.CompilationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.shimady.contest.compiler.model.SolutionStatus.*;

@Slf4j
@Service
public class CompilerService {
    private static final String DIR_PATH = System.getProperty("user.home") + "/cpp";

    private final Duration timeout;
    private final TestCaseService testCaseService;

    public CompilerService(@Value("${compiler.timeoutSeconds}") long timeoutSeconds,
                           TestCaseService testCaseService) {
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        this.testCaseService = testCaseService;
    }

    public CompilationResult compileAndRun(String code, Task task) {
        log.info("Compiling and running code for task with id: {}", task.getId());
        String fileId = UUID.randomUUID().toString();
        Path filePath = Path.of(DIR_PATH, fileId + ".cpp");
        Path executablePath = Path.of(DIR_PATH, fileId);

        try {
            Files.createDirectories(Path.of(DIR_PATH));
            Files.writeString(filePath, code, StandardOpenOption.CREATE);

            if (!compileCode(filePath, executablePath)) {
                return new CompilationResult(COMPILE_ERROR, (short) 0);
            }

            return runTests(executablePath, task);
        } catch (IOException | InterruptedException e) {
            log.error("Error while processing", e);
        } finally {
            cleanup(filePath, executablePath);
        }
        return new CompilationResult(INTERNAL_ERROR, (short) 0);
    }

    private boolean compileCode(Path sourcePath, Path executablePath) throws IOException, InterruptedException {
        Process compilerProcess = strartProcess("g++", sourcePath.toString(), "-o", executablePath.toString());

        if (!compilerProcess.waitFor(timeout.toSeconds(), TimeUnit.SECONDS)) {
            compilerProcess.destroyForcibly();
            log.warn("Compilation timed out");
            return false;
        }

        if (compilerProcess.exitValue() != 0) {
            String errorOutput = new String(compilerProcess.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            log.warn("Compilation failed. Exit code: {}. Output: {}", compilerProcess.exitValue(), errorOutput);
            return false;
        }

        return true;
    }

    private CompilationResult runTests(Path executablePath, Task task) throws IOException, InterruptedException {
        List<TestCase> testCases = testCaseService.getAllTestCasesByTask(task);
        short testsPassed = 0;

        for (TestCase testCase : testCases) {
            Process runnerProcess = strartProcess(executablePath.toString());
            writeInput(runnerProcess, testCase.getInput());

            boolean runnerFinished = runnerProcess.waitFor(timeout.toSeconds(), TimeUnit.SECONDS);
            if (!runnerFinished) {
                runnerProcess.destroyForcibly();
                log.info("Runner process timed out on test: {}", testsPassed + 1);
                return new CompilationResult(TIMED_OUT, testsPassed);
            }

            int runnerExitCode = runnerProcess.exitValue();
            String result = new String(runnerProcess.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
            if (runnerExitCode != 0) {
                log.info("Runner process exited with code: {}, on test: {}, result: {}", runnerExitCode, testsPassed + 1, result);
                return new CompilationResult(RUNTIME_ERROR, testsPassed);
            }

            if (!result.equals(testCase.getOutput())) {
                return new CompilationResult(WRONG_ANSWER, testsPassed);
            }

            testsPassed++;
        }

        SolutionStatus status = testsPassed == task.getTestCasesCount() ? ACCEPTED : WRONG_ANSWER;
        return new CompilationResult(status, testsPassed);
    }

    private static void cleanup(Path... paths) {
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.error("Error while deleting file {}", path, e);
            }
        }
    }

    private static Process strartProcess(String... command) throws IOException {
        log.debug("Starting process with command: {}", String.join(" ", command));
        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }

    private static void writeInput(Process p, String input) throws IOException {
        log.debug("Writing input to process: {}", input);
        try (OutputStream os = p.getOutputStream()) {
            os.write(input.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }
}