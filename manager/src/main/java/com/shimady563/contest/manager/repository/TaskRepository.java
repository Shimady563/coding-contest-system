package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByIdIn(Collection<Long> ids);

    List<Task> findByContestVersions(ContestVersion contestVersion);

    Page<Task> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
