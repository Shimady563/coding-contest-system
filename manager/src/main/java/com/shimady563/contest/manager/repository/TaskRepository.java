package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByIdIn(Collection<Long> ids);

    List<Task> findByContestVersion(ContestVersion contestVersion);
}
