package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
}
