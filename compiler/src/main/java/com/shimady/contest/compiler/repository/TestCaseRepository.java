package com.shimady.contest.compiler.repository;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    Optional<TestCase> findByTask(Task task);
}
