package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    Page<Contest> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Contest> findByGroup(Group group);

    @Query("select c from Contest c left join fetch c.contestVersions where c.id = :id")
    Optional<Contest> findByIdWithContestVersions(@Param("id") Long id);
}
