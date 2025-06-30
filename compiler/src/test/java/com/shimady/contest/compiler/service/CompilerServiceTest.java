//package com.shimady.contest.compiler.service;
//
//import com.shimady.contest.compiler.model.SolutionStatus;
//import com.shimady.contest.compiler.model.Task;
//import com.shimady.contest.compiler.model.TestCase;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.ByteArrayInputStream;
//import java.util.List;
//
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.mockito.Mockito.mock;
//
//@ExtendWith(MockitoExtension.class)
//class CompilerServiceTest {
//
//    @Mock
//    private SolutionService solutionService;
//
//    @Mock
//    private TestCaseService testCaseService;
//
//    @InjectMocks
//    private CompilerService compilerService;
//
//    private final String sampleCode = "int main() { return 0; }";
//    private final Task sampleTask = new Task();
//
//    @BeforeEach
//    void setUp() {
//        sampleTask.setId(1L);
//    }
//
//    @Test
//    void shouldCompileAndRunSuccessfully() {
//        TestCase testCase = new TestCase();
//        testCase.setInput("input");
//        testCase.setOutput("output");
//
//        given(testCaseService.getAllTestCasesByTask(sampleTask)).willReturn(List.of(testCase));
//
//        compilerService.compileAndRun(sampleCode, sampleTask);
//
//        then(solutionService).should().createSolution(sampleCode, SolutionStatus.ACCEPTED, (short) 1, sampleTask);
//    }
//
//    @Test
//    void shouldFailCompilation() {
//
//        compilerService.compileAndRun(sampleCode, sampleTask);
//
//        then(solutionService).should().createSolution(sampleCode, SolutionStatus.COMPILE_ERROR, (short) 0, sampleTask);
//    }
//
//    @Test
//    void shouldFailWithRuntimeError() {
//        TestCase testCase = new TestCase();
//        testCase.setInput("input");
//        testCase.setOutput("output");
//        Process mockCompileProcess = mock(Process.class);
//        Process mockRunProcess = mock(Process.class);
//
//        given(testCaseService.getAllTestCasesByTask(sampleTask)).willReturn(List.of(testCase));
//        given(mockCompileProcess.exitValue()).willReturn(0);
//        given(mockRunProcess.exitValue()).willReturn(1);
//        given(mockRunProcess.getInputStream()).willReturn(new java.io.ByteArrayInputStream("Runtime Error".getBytes()));
//
//        compilerService.compileAndRun(sampleCode, sampleTask);
//
//        then(solutionService).should().createSolution(sampleCode, SolutionStatus.RUNTIME_ERROR, (short) 0, sampleTask);
//    }
//
//    @Test
//    void shouldTimeoutExecution() {
//        TestCase testCase = new TestCase();
//        testCase.setInput("input");
//        testCase.setOutput("output");
//        Process mockCompileProcess = mock(Process.class);
//
//        given(testCaseService.getAllTestCasesByTask(sampleTask)).willReturn(List.of(testCase));
//
//        given(mockCompileProcess.exitValue()).willReturn(0);
//
//        compilerService.compileAndRun(sampleCode, sampleTask);
//
//        then(solutionService).should().createSolution(sampleCode, SolutionStatus.TIMED_OUT, (short) 0, sampleTask);
//    }
//
//    @Test
//    void shouldFailOnWrongAnswer() {
//        TestCase testCase = new TestCase();
//        testCase.setInput("input");
//        testCase.setOutput("output");
//        given(testCaseService.getAllTestCasesByTask(sampleTask)).willReturn(List.of(testCase));
//
//        Process mockCompileProcess = mock(Process.class);
//        given(mockCompileProcess.exitValue()).willReturn(0);
//
//        Process mockRunProcess = mock(Process.class);
//        given(mockRunProcess.exitValue()).willReturn(0);
//        given(mockRunProcess.getInputStream()).willReturn(new java.io.ByteArrayInputStream("wrongOutput".getBytes()));
//
//        compilerService.compileAndRun(sampleCode, sampleTask);
//
//        then(solutionService).should().createSolution(sampleCode, SolutionStatus.WRONG_ANSWER, (short) 0, sampleTask);
//    }
//
//    @Test
//    void shouldHandleInternalError() {
//        given(testCaseService.getAllTestCasesByTask(sampleTask)).willThrow(new RuntimeException("Unexpected Error"));
//
//        compilerService.compileAndRun(sampleCode, sampleTask);
//
//        then(solutionService).should().createSolution(sampleCode, SolutionStatus.INTERNAL_ERROR, (short) 0, sampleTask);
//    }
//}
//
