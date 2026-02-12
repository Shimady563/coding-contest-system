package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;

import java.util.List;

public interface TestCaseService {
    List<TestCase> getAllTestCasesByTask(Task task);
}
