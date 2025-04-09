package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t " +
            "left join fetch t.testCases " +
            "where t.contestVersion = :contestVersion ")
    List<Task> findByContestVersion(ContestVersion contestVersion);
}
