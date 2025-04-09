package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    Page<Contest> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Contest> findByGroup(Group group);
}
