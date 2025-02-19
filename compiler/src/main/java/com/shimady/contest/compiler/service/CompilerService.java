package com.shimady.contest.compiler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.Future;

@Service
@Slf4j
public class CompilerService {
    private static final String DIR_PATH = "/cpp";

    public static void compileAndRun(String code) {
        log.info("Compiling and running code: {}", code);

        String fileId = String.valueOf(UUID.randomUUID());
        Path filePath = Path.of(DIR_PATH, fileId + ".cpp").toAbsolutePath();
        Path executablePath = Path.of(DIR_PATH, fileId).toAbsolutePath();

        try {
            Files.writeString(filePath, code, StandardOpenOption.CREATE);
            compile(filePath, executablePath);
            log.info("{} {}", executablePath, filePath);
            String input = "1 2";
            Process compilatorProcess = startProcess(executablePath);
            Future<String> resultFuture = getResult(compilatorProcess);
            writeInput(compilatorProcess, input);
            String result = resultFuture.get();

            assert result.strip().equals("3");
            log.info("Program execution result: {}", result);
        } catch (Exception e) {
            log.error("Error while executing the code: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void compile(Path filePath, Path executablePath) throws IOException {
        new ProcessBuilder("g++", filePath.toString(), "-o", executablePath.toString())
                .redirectErrorStream(true)
                .start()
                .onExit().thenAccept(process -> {
                    try {
                        if (process.exitValue() != 0) {
                            throw new RuntimeException("Compilation failed: " + new String(process.getInputStream().readAllBytes()));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).join();
    }

    private static Future<String> getResult(Process p) {
        log.info("Getting result from process: {}", p.info());
        return p.onExit().handleAsync((process, exception) -> {
            try {
                if (exception != null) {
                    throw new RuntimeException(exception);
                }
                if (process.exitValue() != 0) {
                    throw new RuntimeException("Process exited with code: " + process.exitValue());
                }
                return new String(process.getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Process startProcess(Path executablePath) throws IOException {
        log.info("Starting process: {}", executablePath);
        return new ProcessBuilder(executablePath.toString())
                .redirectErrorStream(true)
                .start();
    }

    private static void writeInput(Process p, String input) {
        log.info("Writing input to process: {}", input);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()))) {
            writer.write(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
