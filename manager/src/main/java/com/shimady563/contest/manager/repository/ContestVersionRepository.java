package com.shimady563.contest.manager.repository;

import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContestVersionRepository extends JpaRepository<ContestVersion, Long> {
    List<ContestVersion> findByContest(Contest contest);

    @Query("select cv from ContestVersion cv left join fetch cv.users")
    Optional<ContestVersion> findByIdFetchUsers(Long id);
}
