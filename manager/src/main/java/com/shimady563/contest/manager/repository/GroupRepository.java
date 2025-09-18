package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.Group;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Page<Group> findByNameContainingIgnoreCase(String name, Pageable pageRequest);
}
