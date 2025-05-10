package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import com.shimady.contest.compiler.repository.TestCaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TestCaseServiceTest {
    @Mock
    private TestCaseRepository testCaseRepository;

    @InjectMocks
    private TestCaseService testCaseService;

    @Test
    void shouldGetAllTestCasesByTask() {
        var testCase1 = new TestCase();
        testCase1.setInput("Input1");
        var testCase2 = new TestCase();
        testCase2.setInput("Input2");
        var task = new Task();
        task.setName("Task");

        given(testCaseRepository.findAllByTask(task)).willReturn(List.of(testCase1, testCase2));

        var testCases = testCaseService.getAllTestCasesByTask(task);

        assertThat(testCases).hasSize(2)
                .extracting(TestCase::getInput)
                .containsExactly(testCase1.getInput(), testCase2.getInput());
    }
}
