package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.SolutionStatus;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import com.shimady.contest.compiler.model.dto.CompilationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
    private final String workDir;
    private final int maxBytesOutput;
    private final Duration timeout;
    private final TestCaseService testCaseService;

    public CompilerService(@Value("${compiler.timeoutSeconds}") long timeoutSeconds,
                           @Value("${compiler.maxOutputBytes}") int maxOutputBytes,
                           @Value("${compiler.maxOutputBytes}") String workDir,
                           TestCaseService testCaseService) {
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        this.maxBytesOutput = maxOutputBytes;
        this.workDir = System.getProperty("user.home") + workDir + "/";
        this.testCaseService = testCaseService;
    }

    public CompilationResult compileAndRun(String code, Task task) {
        log.info("Compiling and running code for task with id: {}", task.getId());
        String fileId = UUID.randomUUID().toString();
        Path runDir = Path.of(workDir, fileId);
        Path sourcePath = runDir.resolve(fileId + ".cpp");
        Path executablePath = runDir.resolve(fileId);

        try {
            Files.createDirectories(runDir);
            Files.writeString(sourcePath, code, StandardOpenOption.CREATE);

            if (!compileCode(runDir, sourcePath, executablePath)) {
                return new CompilationResult(COMPILE_ERROR, (short) 0);
            }

            return runTests(runDir, executablePath, task);
        } catch (IOException | InterruptedException e) {
            log.error("Error while processing", e);
        } finally {
            cleanupDirectory(runDir);
        }
        return new CompilationResult(INTERNAL_ERROR, (short) 0);
    }

    private boolean compileCode(Path runDir, Path sourcePath, Path executablePath) throws IOException, InterruptedException {
        Process compilerProcess = startProcess(runDir, "g++", sourcePath.getFileName().toString(), "-O2", "-s", "-o", executablePath.getFileName().toString());

        if (!compilerProcess.waitFor(timeout.toSeconds(), TimeUnit.SECONDS)) {
            compilerProcess.destroyForcibly();
            log.warn("Compilation timed out");
            return false;
        }

        if (compilerProcess.exitValue() != 0) {
            String errorOutput = readLimited(compilerProcess.getInputStream());
            log.warn("Compilation failed. Exit code: {}. Output: {}", compilerProcess.exitValue(), errorOutput);
            return false;
        }

        return true;
    }

    private CompilationResult runTests(Path runDir, Path executablePath, Task task) throws IOException, InterruptedException {
        List<TestCase> testCases = testCaseService.getAllTestCasesByTask(task);
        short testsPassed = 0;

        for (TestCase testCase : testCases) {
            Process runnerProcess = startProcess(runDir, executablePath.toString());
            writeInput(runnerProcess, testCase.getInput());

            boolean runnerFinished = runnerProcess.waitFor(timeout.toSeconds(), TimeUnit.SECONDS);
            if (!runnerFinished) {
                runnerProcess.destroyForcibly();
                log.info("Runner process timed out on test: {}", testsPassed + 1);
                return new CompilationResult(TIMED_OUT, testsPassed);
            }

            int runnerExitCode = runnerProcess.exitValue();
            String result = readLimited(runnerProcess.getInputStream()).strip();
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

    private static void cleanupDirectory(Path directory) {
        if (directory == null) return;
        try {
            if (Files.exists(directory)) {
                Files.walk(directory)
                        .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                log.warn("Failed to delete {}", path);
                            }
                        });
            }
        } catch (IOException e) {
            log.warn("Failed to cleanup directory {}", directory);
        }
    }

    private static Process startProcess(Path workingDir, String... command) throws IOException {
        log.debug("Starting process in {} with command: {}", workingDir, String.join(" ", command));
        ProcessBuilder builder = new ProcessBuilder(command);
        if (workingDir != null) {
            builder.directory(workingDir.toFile());
        }
        builder.redirectErrorStream(true);
        return builder.start();
    }

    private static void writeInput(Process p, String input) throws IOException {
        log.debug("Writing input to process: {}", input);
        try (OutputStream os = p.getOutputStream()) {
            os.write(input.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }

    private String readLimited(InputStream is) throws IOException {
        byte[] buffer = is.readNBytes(maxBytesOutput + 1);
        if (buffer.length > maxBytesOutput) {
            return new String(buffer, 0, maxBytesOutput, StandardCharsets.UTF_8);
        }
        return new String(buffer, StandardCharsets.UTF_8);
    }
}