package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolutionRepository extends JpaRepository<Solution, Long>, JpaSpecificationExecutor<Solution> {
}
