package com.shimady.contest.compiler.repository;

import com.shimady.contest.compiler.model.Solution;
import com.shimady.contest.compiler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findAllByTask(Task task);
}
