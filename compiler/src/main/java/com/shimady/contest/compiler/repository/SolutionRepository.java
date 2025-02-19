package com.shimady.contest.compiler.repository;

import com.shimady.contest.compiler.model.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
}
