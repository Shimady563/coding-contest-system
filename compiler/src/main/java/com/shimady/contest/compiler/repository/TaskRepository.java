package com.shimady.contest.compiler.repository;

import com.shimady.contest.compiler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
