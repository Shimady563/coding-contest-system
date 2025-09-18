package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.SolutionStatus;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CompilerServiceTest {
    private static final int TIMEOUT_SECONDS = 5;
    private static final int MAX_OUTPUT_BYTES = 1_000_000;
    private static final String workdir = "/cpp";

    private final Process process = mock(Process.class);
    private final TestCaseService testCaseService = mock(TestCaseService.class);
    private final CompilerService compilerService = new CompilerService(
            TIMEOUT_SECONDS,
            MAX_OUTPUT_BYTES,
            workdir,
            testCaseService
    );

    private static List<TestCase> testCases;
    private static final Task task = new Task();

    @BeforeAll
    public static void init() {
        TestCase tc1 = new TestCase();
        tc1.setId(1L);
        tc1.setInput("1 2");
        tc1.setOutput("3");

        TestCase tc2 = new TestCase();
        tc2.setId(2L);
        tc2.setInput("15 13");
        tc2.setOutput("28");

        testCases = List.of(tc1, tc2);

        task.setId(1L);
        task.setTestCases(Set.of(tc1, tc2));
        task.setTestCasesCount((short) task.getTestCases().size());
    }

    @Test
    void shouldCompileAndRunSuccessfully() {
        var code = """
                #include <iostream>
                using namespace std;
                int main() {
                  	int a, b;
                  	cin >> a >> b;
                  	cout << a + b;
                    return 0;
                }
                """;

        given(testCaseService.getAllTestCasesByTask(task)).willReturn(testCases);

        var result = compilerService.compileAndRun(code, task);

        assertEquals(SolutionStatus.ACCEPTED, result.status());
        assertEquals(2, result.testsPassed());
    }

    @Test
    void shouldFailCompilation() {
        var code = """
                #include <iostream>
                using namespace std;
                int main() {
                  	int a, b;
                  	cin >> a >> b;
                  	cout << a + b;
                    return 0
                }
                """;

        var result = compilerService.compileAndRun(code, task);

        then(testCaseService).shouldHaveNoInteractions();
        assertEquals(SolutionStatus.COMPILE_ERROR, result.status());
        assertEquals(0, result.testsPassed());
    }

    @Test
    void shouldFailWithRuntimeError() {
        var code = """
                #include <iostream>
                using namespace std;
                int main() {
                    cout << 1 / 0;
                    return 0;
                }
                """;

        given(testCaseService.getAllTestCasesByTask(task)).willReturn(testCases);

        var result = compilerService.compileAndRun(code, task);

        assertEquals(SolutionStatus.RUNTIME_ERROR, result.status());
        assertEquals(0, result.testsPassed());
    }

    @Test
    void shouldTimeoutExecution() {
        var code = """
                #include <iostream>
                using namespace std;
                int main() {
                    while (true) {}
                    return 0;
                }
                """;

        given(testCaseService.getAllTestCasesByTask(task)).willReturn(testCases);

        var result = compilerService.compileAndRun(code, task);

        assertEquals(SolutionStatus.TIMED_OUT, result.status());
        assertEquals(0, result.testsPassed());
    }

    @Test
    void shouldFailOnWrongAnswer() {
        var code = """
                #include <iostream>
                using namespace std;
                int main() {
                  	int a, b;
                  	cin >> a >> b;
                  	cout << a - b;
                    return 0;
                }
                """;

        given(testCaseService.getAllTestCasesByTask(task)).willReturn(testCases);

        var result = compilerService.compileAndRun(code, task);

        assertEquals(SolutionStatus.WRONG_ANSWER, result.status());
        assertEquals(0, result.testsPassed());
    }

    @Test
    void shouldFailCompilationWhenCompilerTimesOut() throws Exception {
        var code = """
                #include <iostream>
                using namespace std;
                int main() {
                  	int a, b;
                  	cin >> a >> b;
                  	cout << a + b;
                  	return 0;
                }
                """;

        given(process.waitFor(eq(TIMEOUT_SECONDS), any(TimeUnit.class))).willReturn(false);

        try (MockedConstruction<ProcessBuilder> mocked = Mockito.mockConstruction(ProcessBuilder.class,
                (builderMock, context) -> {
                    given(builderMock.redirectErrorStream(true)).willReturn(builderMock);
                    given(builderMock.start()).willReturn(process);
                })) {

            var result = compilerService.compileAndRun(code, task);

            assertEquals(SolutionStatus.COMPILE_ERROR, result.status());
            assertEquals(0, result.testsPassed());
            then(testCaseService).shouldHaveNoInteractions();
        }
    }

    @Test
    void shouldReturnInternalErrorWhenIOExceptionIsThrown() {
        var code = """
                #include <iostream>
                using namespace std;
                int main(){ return 0; }
                """;

        try (MockedConstruction<ProcessBuilder> mocked = Mockito.mockConstruction(ProcessBuilder.class,
                (builderMock, context) -> {
                    given(builderMock.redirectErrorStream(true)).willReturn(builderMock);
                    given(builderMock.start()).willThrow(new IOException());
                })) {

            var result = compilerService.compileAndRun(code, task);

            assertEquals(SolutionStatus.INTERNAL_ERROR, result.status());
            assertEquals(0, result.testsPassed());
            then(testCaseService).shouldHaveNoInteractions();
        }
    }
}
