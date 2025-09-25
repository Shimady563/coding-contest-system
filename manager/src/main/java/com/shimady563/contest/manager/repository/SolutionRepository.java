package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.Solution;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface SolutionRepository extends JpaRepository<Solution, Long>, JpaSpecificationExecutor<Solution> {
    List<Solution> findByUserAndTask(User foundUser, Task task);

    void deleteBySubmittedAtBefore(LocalDateTime instant);
}
