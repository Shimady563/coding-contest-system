package com.shimady.contest.compiler.repository;

import com.shimady.contest.compiler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
