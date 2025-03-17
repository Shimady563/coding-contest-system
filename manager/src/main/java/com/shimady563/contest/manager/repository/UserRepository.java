package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    @EntityGraph(type = EntityGraph.EntityGraphType.LOAD, attributePaths = "group")
    Page<User> findFetchGroupAll(Specification<User> spec, Pageable pageable);
}
